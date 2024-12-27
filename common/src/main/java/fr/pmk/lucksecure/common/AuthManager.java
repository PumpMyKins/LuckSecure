package fr.pmk.lucksecure.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import com.amdelamar.jotp.OTP;
import com.amdelamar.jotp.type.Type;

import fr.pmk.lucksecure.common.database.LuckSecureDatabase;
import fr.pmk.lucksecure.common.database.UserToken;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.Context;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.query.QueryOptions;

public class AuthManager {
        private List<UUID> authentificatedUsers;

        protected Logger logger;
        private LuckSecureDatabase database;
        protected LuckPerms luckPerms;

        protected AuthManager(Logger logger, LuckSecureDatabase database, LuckPerms luckPerms) {
                this.authentificatedUsers = new ArrayList<>();

                this.logger = logger;
                this.database = database;
                this.luckPerms = luckPerms;
        }

        // Login Event
        public final void onPlayerPostLoginEvent(Audience audience) {
                Optional<UUID> id = audience.get(Identity.UUID);
                if (id.isPresent() && this.unauthenticatedUser(audience)) {
                        Component name = audience.getOrDefault(Identity.DISPLAY_NAME, Component.text("null"));
                        this.logger.fine(name + "/" + id.get() + " cleaned up from authenticated players. (disconnect event missed ?)");
                }
        }

        // Left Event
        public final void onPlayerDisconnectEvent(Audience audience) {
                Optional<UUID> id = audience.get(Identity.UUID);
                if (id.isPresent() && this.unauthenticatedUser(audience)) { // AUTHENTICATED USERS LIST CLEAR
                        Component name = audience.getOrDefault(Identity.DISPLAY_NAME, Component.text("null"));
                        this.logger.fine(name + "/" + id.get() + " has been removed from the authenticated players.");
                }
        }

        public boolean isAuthenticated(Audience audience) {
                Optional<UUID> id = audience.get(Identity.UUID);
                if (id.isPresent()) {
                        return this.authentificatedUsers.contains(id.get());
                }
                return false;
        }

        public boolean authenticatedUser(Audience audience) {
                Optional<UUID> id = audience.get(Identity.UUID);
                if (id.isEmpty()) {
                        throw new IllegalStateException("Audiance UUID can't be empty");
                }
                return this.authentificatedUsers.add(id.get());
        }

        public boolean unauthenticatedUser(Audience audience) {
                Optional<UUID> id = audience.get(Identity.UUID);
                if (id.isEmpty()) {
                        throw new IllegalStateException("Audiance UUID can't be empty");
                }
                return this.authentificatedUsers.remove(id.get());
        }

        public final boolean doesUserHavePermWithAuthContext(Audience audience) { // CHECK IF SOME OF USER'S GROUP OR PERMISSION NEED CONTEXT AUTHENTICATED
                Optional<UUID> id = audience.get(Identity.UUID);
                if (id.isPresent()) {
                        User user = this.luckPerms.getUserManager().getUser(id.get());
                        Collection<Node> nodes = user.resolveInheritedNodes(QueryOptions.nonContextual()); // GET USER NODES
                        for (Node node : nodes) {
                                for (Context context : node.getContexts()) { // CHECK NODE CONTEXT
                                        if (context.getKey().equalsIgnoreCase(AuthContextCalculator.KEY)
                                                        && context.getValue().equalsIgnoreCase(AuthContextCalculator.AUTH)) {
                                                return true;
                                        }
                                }
                        }
                }
                return false;
        }

        public final boolean doesUserHaveTotpSetup(Audience audience) throws SQLException { // CHECK IF BDD CONTAINS TOTP USER KEY
                return this.getUserTotpSecret(audience) != null;
        }

        public String getUserTotpSecret(Audience audience) throws SQLException { // GET TOKEN KEY WITH USER UUID
                Optional<UUID> id = audience.get(Identity.UUID);
                if (id.isEmpty()) {
                        throw new IllegalStateException("Audiance UUID can't be empty");
                }

                UserToken userTotp = this.database.getTotpDao().queryForId(id.get());
                return userTotp == null ? null : userTotp.getToken();
        }

        public String generateUserTotpSecret(Audience audience) throws SQLException { // CREATE A NEW TOTP ENTRY IN BDD & RETURN THE KEY
                Optional<UUID> id = audience.get(Identity.UUID);
                if (id.isEmpty()) {
                        throw new IllegalStateException("Audiance UUID can't be empty");
                }

                String secret = OTP.randomBase32(20); // GENERATE SECRET
                UserToken userTotp = new UserToken(id.get(), secret);

                this.database.getTotpDao().create(userTotp);

                return secret;
        }

        public boolean deleteUserTotpSecret(Audience audience) throws SQLException {
                Optional<UUID> id = audience.get(Identity.UUID);
                if (id.isEmpty()) {
                        throw new IllegalStateException("Audiance UUID can't be empty");
                }

                this.onPlayerDisconnectEvent(audience);
                return this.database.getTotpDao().deleteById(id.get()) == 1;
        }

        public boolean isUserTotpValid(Audience audience, String code) throws SQLException, InvalidKeyException, IllegalArgumentException, NoSuchAlgorithmException, IOException {
                Optional<UUID> id = audience.get(Identity.UUID);
                if (id.isEmpty()) {
                        throw new IllegalStateException("Audiance UUID can't be empty");
                }

                String name = audience.getOrDefault(Identity.DISPLAY_NAME, Component.text("null")).toString();
                String secret = this.getUserTotpSecret(audience);

                boolean result = false;
                if (OTP.verify(secret, OTP.timeInHex(System.currentTimeMillis()), code, 6, Type.TOTP)) {
                        result = true;
                        this.authentificatedUsers.add(id.get()); // ADD THE PLAYER TO THE AUTHENTICATED USERS
                        this.logger.info(name + "/" + id.get() + " authentication succed.");
                } else {
                        this.logger.warning(name + "/" + id.get() + " authentication attempt failed.");
                }
                return result;
        }

        /*
         * GETTER
         */
        public final LuckPerms getLuckPerms() {
                return this.luckPerms;
        }

        public static String generateUserTokenUrl(String issuer, String secret, String label) {
                String url = OTP.getURL(secret, 6, Type.TOTP, issuer, label);
                try {
                        url = URLEncoder.encode(url, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                }
                return "https://api.qrserver.com/v1/create-qr-code/?data=" + url;
        }

}
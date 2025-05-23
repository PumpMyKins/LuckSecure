package fr.pmk.lucksecure.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.commons.configuration2.YAMLConfiguration;

import com.amdelamar.jotp.OTP;
import com.amdelamar.jotp.type.Type;

import fr.pmk.lucksecure.common.database.LuckSecureDatabase;
import fr.pmk.lucksecure.common.database.UserToken;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.Context;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.query.QueryOptions;

public class AuthManager {
        private List<UUID> authenticatedUsers;
        private HashMap<UUID, PreviousConnectionInfo> previousConnectionInfos;

        protected final Logger logger;
        private final LuckSecureDatabase database;
        protected final LuckPerms luckPerms;
        private final Config config;

        protected AuthManager(Logger logger, LuckSecureDatabase database, LuckPerms luckPerms, Config config) {
                this.authenticatedUsers = new ArrayList<>();
                this.previousConnectionInfos = new HashMap<>();

                this.logger = logger;
                this.database = database;
                this.luckPerms = luckPerms;
                this.config = config;
        }

        // Login Event
        public final void onPlayerPostLoginEvent(Audience audience, InetAddress audienceAddr) {
                Optional<UUID> id = audience.get(Identity.UUID);
                if (id.isEmpty()) {
                        return;
                }

                if (!this.isAuthenticated(audience)) {
                        return;
                }

                PreviousConnectionInfo previous = this.previousConnectionInfos.get(id.get());
                if (previous == null 
                || !previous.getAddress().equals(audienceAddr) 
                || (System.currentTimeMillis() - previous.getDisconnectionTime()) > (this.config.authDisconnectionGraceTime * 60000)) {
                        if (this.unauthenticateUser(audience)) {
                                String name = audience.getOrDefault(Identity.NAME, null);
                                this.logger.info(name + "/" + id.get() + " cleaned up from authenticated players.");
                        }
                }

        }

        // Left Event
        public final void onPlayerDisconnectEvent(Audience audience, InetAddress audienceAddr) {
                Optional<UUID> id = audience.get(Identity.UUID);
                if (id.isEmpty()) {
                        return;
                }

                if (this.config.authDisconnectionGraceTime == 0) {
                        if (this.unauthenticateUser(audience)) { // AUTHENTICATED USERS LIST CLEAR
                                String name = audience.getOrDefault(Identity.NAME, null);
                                this.logger.info(name + "/" + id.get() + " has been removed from the authenticated players.");
                        }
                } else {
                        this.previousConnectionInfos.put(id.get(), new PreviousConnectionInfo(System.currentTimeMillis(), audienceAddr));
                }


        }

        public boolean isAuthenticated(Audience audience) {
                Optional<UUID> id = audience.get(Identity.UUID);
                if (id.isPresent()) {
                        return this.authenticatedUsers.contains(id.get());
                }
                return false;
        }

        public boolean authenticateUser(Audience audience) {
                Optional<UUID> id = audience.get(Identity.UUID);
                if (id.isEmpty()) {
                        throw new IllegalStateException("Audiance UUID can't be empty");
                }
                return this.authenticatedUsers.add(id.get());
        }

        public boolean unauthenticateUser(Audience audience) {
                Optional<UUID> id = audience.get(Identity.UUID);
                if (id.isEmpty()) {
                        throw new IllegalStateException("Audiance UUID can't be empty");
                }
                return this.authenticatedUsers.remove(id.get());
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

                this.unauthenticateUser(audience);
                return this.database.getTotpDao().deleteById(id.get()) == 1;
        }

        public boolean isUserTotpValid(Audience audience, String code) throws SQLException, InvalidKeyException, IllegalArgumentException, NoSuchAlgorithmException, IOException {
                Optional<UUID> id = audience.get(Identity.UUID);
                if (id.isEmpty()) {
                        throw new IllegalStateException("Audiance UUID can't be empty");
                }

                String name = audience.getOrDefault(Identity.NAME, null);
                String secret = this.getUserTotpSecret(audience);

                boolean result = false;
                if (OTP.verify(secret, OTP.timeInHex(System.currentTimeMillis()), code, 6, Type.TOTP)) {
                        result = true;
                        this.authenticatedUsers.add(id.get()); // ADD THE PLAYER TO THE AUTHENTICATED USERS
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

        public final Logger getLogger() {
            return logger;
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

        public static final class Config {
        
                private final int authDisconnectionGraceTime;
                private final boolean authIpAddrMatch;

                public Config(YAMLConfiguration config) {
                        this.authDisconnectionGraceTime = config.getInt("authentication_disconnection_grace_time", 0);
                        this.authIpAddrMatch = config.getBoolean("authentication_ip_addr_match", true);
                }

                public int getAuthDisconnectionGraceTime() {
                    return this.authDisconnectionGraceTime;
                }

                public boolean getAuthIpAddrMatch() {
                        return this.authIpAddrMatch;
                }

        }

        public static final class PreviousConnectionInfo {
        
                private final long disconnectionTime;
                private final InetAddress address;

                public PreviousConnectionInfo(long disconnectionTime, InetAddress address) {
                        this.disconnectionTime = disconnectionTime;
                        this.address = address;
                } 

                public long getDisconnectionTime() {
                    return this.disconnectionTime;
                }

                public InetAddress getAddress() {
                    return this.address;
                }
        }
}
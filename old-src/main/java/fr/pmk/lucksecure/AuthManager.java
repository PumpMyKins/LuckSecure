package fr.pmk.lucksecure;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import com.amdelamar.jotp.OTP;
import com.amdelamar.jotp.type.Type;

import fr.pmk.lucksecure.data.LuckSecureDatabase;
import fr.pmk.lucksecure.data.UserTotp;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.Context;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.query.QueryOptions;

public abstract class AuthManager<T, A extends IAuthUserAdapter> {
        private List<UUID> authentificatedUsers;

        private Logger logger;
        private LuckSecureDatabase database;
        private LuckPerms api;

        public AuthManager(Logger logger, LuckSecureDatabase database, LuckPerms api) {
                this.authentificatedUsers = new ArrayList<>();

                this.logger = logger;
                this.database = database;
                this.api = api;
        }

        public abstract A getPlayerAdapter(T player);

        // Login Event
        public final void onPlayerPostLoginEvent(T player) {
                UUID id = this.getPlayerAdapter(player).getUniqueId();
                if (this.authentificatedUsers.remove(id)) {
                        String name = this.getPlayerAdapter(player).getName();
                        this.logger.fine(name + "/" + id + " cleaned up from authenticated players. (disconnect event missed ?)");
                }
        }

        // Left Event
        public final void onPlayerDisconnectEvent(T player) {
                UUID id = this.getPlayerAdapter(player).getUniqueId();
                if (this.authentificatedUsers.remove(id)) { // AUTHENTICATED USERS LIST CLEAR
                        String name = this.getPlayerAdapter(player).getName();
                        this.logger.fine(name + "/" + id + " has been removed from the authenticated players.");
                }
        }

        public final boolean doesUserHavePermWithAuthContext(T player) { // CHECK IF SOME OF USER'S GROUP OR PERMISSION
                                                                         // NEED CONTEXT AUTHENTICATED
                User user = this.getPlayerAdapter(player).getLuckPermsUser();
                Collection<Node> nodes = user.resolveInheritedNodes(QueryOptions.nonContextual()); // GET USER NODES
                for (Node node : nodes) {
                        for (Context context : node.getContexts()) { // CHECK NODE CONTEXT
                                if (context.getKey().equalsIgnoreCase(AuthContextCalculator.KEY)
                                                && context.getValue().equalsIgnoreCase(AuthContextCalculator.AUTH)) {
                                        return true;
                                }
                        }
                }
                return false;
        }

        public final boolean doesUserHaveTotpSetup(T player) throws SQLException { // CHECK IF BDD CONTAINS TOTP USER
                                                                                   // KEY
                return this.getUserTotpSecret(player) != null;
        }

        public final String getUserTotpSecret(T player) throws SQLException { // GET TOKEN KEY WITH USER UUID
                UUID id = this.getPlayerAdapter(player).getUniqueId();
                UserTotp userTotp = this.database.getTotpDao().queryForId(id);
                return userTotp == null ? null : userTotp.getToken();
        }

        public final String generateUserTotpSecret(T player) throws SQLException { // CREATE A NEW TOTP ENTRY IN BDD &
                                                                                   // RETURN THE KEY
                UUID id = this.getPlayerAdapter(player).getUniqueId();
                String secret = OTP.randomBase32(20); // GENERATE SECRET
                UserTotp userTotp = new UserTotp(id, secret);

                this.database.getTotpDao().create(userTotp);

                return secret;
        }

        public final boolean deleteUserTotpSecret(T player) throws SQLException {
                UUID id = this.getPlayerAdapter(player).getUniqueId();
                this.onPlayerDisconnectEvent(player);
                return this.database.getTotpDao().deleteById(id) == 1;
        }

        public final boolean isUserTotpValid(T player, String code) throws SQLException, InvalidKeyException,
                        IllegalArgumentException, NoSuchAlgorithmException, IOException {
                UUID id = this.getPlayerAdapter(player).getUniqueId();
                String name = this.getPlayerAdapter(player).getName();
                String secret = this.getUserTotpSecret(player);
                boolean result = false;
                if (OTP.verify(secret, OTP.timeInHex(System.currentTimeMillis()), code, 6, Type.TOTP)) {
                        result = true;
                        this.authentificatedUsers.add(id); // ADD THE PLAYER TO THE AUTHENTICATED USERS
                        this.logger.info(name + "/" + id + " authentication succed.");
                } else {
                        this.logger.warning(name + "/" + id + " authentication attempt failed.");
                }
                return result;
        }

        public void setupLuckPermsContext() {
                AuthContextCalculator<T> calculator = new AuthContextCalculator<T>(this);
                this.api.getContextManager().registerCalculator(calculator); // REGISTER LuckSecure Context
        }

        /*
         * GETTER
         */
        public final List<UUID> getAuthentificatedUsers() {
                return this.authentificatedUsers;
        }

        public final LuckPerms getLuckPerms() {
                return this.api;
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
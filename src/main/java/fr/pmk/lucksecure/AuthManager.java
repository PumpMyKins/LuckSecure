package fr.pmk.lucksecure;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.amdelamar.jotp.OTP;
import com.amdelamar.jotp.type.Type;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.Context;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.query.QueryOptions;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class AuthManager implements Listener 
{
        private List<String> authentificatedUsers;
        
        private MainLuckSecure plugin;

        public AuthManager(MainLuckSecure plugin) {
                this.authentificatedUsers = new ArrayList<>();

                this.plugin = plugin;
        }

        // Join Event
        @EventHandler
        public void onPostLogin(PostLoginEvent event) {
                ProxiedPlayer player = event.getPlayer();
                if (doesUserHaveGroupWithAuthContext(player)) { // CHECK IF USER HAS GROUP OR PERMISSION WHICH NEED AUTHENTICATED CONTEXT
                        AuthCommand.help(player); // DISPLAY HELP
                        MainLuckSecure.LOGGER.info(player.getName() + "/" + player.getUniqueId() + " authentication needed.");
                }
        }

        // Left Event
        @EventHandler
        public void onPlayerDisconnectEvent(PlayerDisconnectEvent event) {
                ProxiedPlayer player = event.getPlayer();
                String playerUUID = player.getUniqueId().toString();

                if (this.authentificatedUsers.remove(playerUUID)) { // AUTHENTICATED USERS LIST CLEAR
                        MainLuckSecure.LOGGER.info(player.getName() + " has been removed from the authenticated players.");
                }
        }

        public boolean doesUserHaveGroupWithAuthContext(ProxiedPlayer player) { // CHECK IF SOME OF USER'S GROUP OR PERMISSION NEED CONTEXT AUTHENTICATED
                LuckPerms luckPerms = this.plugin.getLuckPerms();
                User user = luckPerms.getPlayerAdapter(ProxiedPlayer.class).getUser(player);
                Collection<Node> nodes = user.resolveInheritedNodes(QueryOptions.nonContextual()); // GET USER NODES
                for (Node node : nodes) {
                        for (Context context : node.getContexts()) { // CHECK NODE CONTEXT
                                if (context.getKey().equalsIgnoreCase(AuthContextCalculator.KEY) && context.getValue().equalsIgnoreCase(AuthContextCalculator.AUTH)) {
                                        return true;
                                }
                        }
                }
                return false;
        }

        public boolean doesUserHasSetupA2F(ProxiedPlayer player) throws SQLException { // CHECK IF BDD CONTAINS TOTP USER KEY
                return this.getUserTokenA2F(player) != null;
        }

        public String getUserTokenA2F(ProxiedPlayer player) throws SQLException { // GET TOKEN KEY WITH USER UUID
                SQLiteJDBC jdbc = this.plugin.getJdbc();
                PreparedStatement stmt = jdbc.getConnection().prepareStatement("SELECT token FROM tokens WHERE uuid = ?");
                stmt.setString(0, player.getUniqueId().toString());
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                        rs.close();
                        stmt.close();
                        return null;
                }

                String result = rs.getString("token");

                rs.close();
                stmt.close();
                
                return result;
        }

        public String generateUserTokenA2F(ProxiedPlayer player) throws SQLException { // CREATE A NEW TOTP ENTRY IN BDD & RETURN THE KEY
                String secret = OTP.randomBase32(20); // GENERATE SECRET

                SQLiteJDBC jdbc = this.plugin.getJdbc();
                PreparedStatement stmt = jdbc.getConnection().prepareStatement("INSERT INTO tokens (uuid, token) VALUES(?,?)");
                stmt.setString(1, player.getUniqueId().toString());
                stmt.setString(2, secret);

                stmt.executeUpdate();

                stmt.close();

                return secret;
        }

        public boolean deleteUserTokenA2F(String uuid) throws SQLException {
                SQLiteJDBC jdbc = this.plugin.getJdbc();
                PreparedStatement stmt = jdbc.getConnection().prepareStatement("DELETE from tokens WHERE uuid = ?");
                stmt.setString(1, uuid);

                int result = stmt.executeUpdate();

                stmt.close();

                return result == 1 ? true : false;
        }

        public boolean isUserTokenA2FValid(ProxiedPlayer player, String code) throws SQLException, InvalidKeyException, IllegalArgumentException, NoSuchAlgorithmException, IOException {
                String secret = this.getUserTokenA2F(player);
                boolean result = false;
                if (OTP.verify(secret, OTP.timeInHex(System.currentTimeMillis()), code, 6, Type.TOTP)) {
                        result = true;
                        this.authentificatedUsers.add(player.getUniqueId().toString()); // ADD THE PLAYER TO THE AUTHENTICATED USERS
                        MainLuckSecure.LOGGER.info(player.getName() + "/" + player.getUniqueId() + " authentication succed.");
                } else {
                        MainLuckSecure.LOGGER.warning(player.getName() + "/" + player.getUniqueId() + " authentication attempt failed.");
                }
                return result;
        }
        /*
         * GETTER
         */
        public List<String> getAuthentificatedUsers() {
            return authentificatedUsers;
        }

        public static String generateUserTokenUrl(String issuer, String secret, String label) {
                return "https://chart.googleapis.com/chart?chs=200x200&cht=qr&chl=200x200&chld=M|0&cht=qr&chl=otpauth://totp/" + issuer + ":" + label + "?secret=" + secret + "&issuer=" + issuer + "&algorithm=SHA1&digits=6&period=30"; 
        }

}
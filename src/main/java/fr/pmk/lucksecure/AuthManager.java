package fr.pmk.lucksecure;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

        // Login Event
        @EventHandler
        public void onPlayerPostLoginEvent(PostLoginEvent event) {
                ProxiedPlayer player = event.getPlayer();
                String playerUuid = player.getUniqueId().toString();
                if (this.authentificatedUsers.contains(playerUuid)) {
                        this.authentificatedUsers.remove(playerUuid);
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

        public boolean doesUserHasSetup2AF(ProxiedPlayer player) throws SQLException { // CHECK IF BDD CONTAINS TOTP USER KEY
                return this.getUserToken2AF(player) != null;
        }

        public String getUserToken2AF(ProxiedPlayer player) throws SQLException { // GET TOKEN KEY WITH USER UUID
                SQLiteJDBC jdbc = this.plugin.getJdbc();
                PreparedStatement stmt = jdbc.getConnection().prepareStatement("SELECT token FROM tokens WHERE uuid = ?");
                stmt.setString(1, player.getUniqueId().toString());
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

        public String generateUserToken2AF(ProxiedPlayer player) throws SQLException { // CREATE A NEW TOTP ENTRY IN BDD & RETURN THE KEY
                String secret = OTP.randomBase32(20); // GENERATE SECRET

                SQLiteJDBC jdbc = this.plugin.getJdbc();
                PreparedStatement stmt = jdbc.getConnection().prepareStatement("INSERT INTO tokens (uuid, token) VALUES(?,?)");
                stmt.setString(1, player.getUniqueId().toString());
                stmt.setString(2, secret);

                stmt.executeUpdate();

                stmt.close();

                return secret;
        }

        public boolean deleteUserToken2AF(String uuid) throws SQLException {
                SQLiteJDBC jdbc = this.plugin.getJdbc();
                PreparedStatement stmt = jdbc.getConnection().prepareStatement("DELETE from tokens WHERE uuid = ?");
                stmt.setString(1, uuid);

                int result = stmt.executeUpdate();

                if (this.authentificatedUsers.contains(uuid)) {
                        this.authentificatedUsers.remove(uuid);
                }

                stmt.close();

                return result == 1 ? true : false;
        }

        public boolean isUserToken2AFValid(ProxiedPlayer player, String code) throws SQLException, InvalidKeyException, IllegalArgumentException, NoSuchAlgorithmException, IOException {
                String secret = this.getUserToken2AF(player);
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
                String url = OTP.getURL(secret, 6, Type.TOTP, "", label);
                try {
                        url = URLEncoder.encode(url, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                }
                return "https://chart.googleapis.com/chart?chs=400x400&cht=qr&chl=400x400&cht=qr&chl=" + url; 
        }

}
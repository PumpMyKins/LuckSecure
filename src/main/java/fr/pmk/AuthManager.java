package fr.pmk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.luckperms.api.LuckPerms;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class AuthManager implements Listener 
{
        private List<String> authentificatedUsers;
        private HashMap<String, Integer> failedAttempts;
        
        private MainLuckSecure plugin;

        public AuthManager(MainLuckSecure plugin) {
                this.authentificatedUsers = new ArrayList<>();
                this.failedAttempts = new HashMap<>();

                this.plugin = plugin;
        }

        // Join Event
        @EventHandler
        public void onPostLogin(PostLoginEvent event) {
                ProxiedPlayer player = event.getPlayer();
                if (doesUserHaveGroupWithAuthContext(player)) { // CHECK IF USER HAS GROUP OR PERMISSION WHICH NEED AUTHENTICATED CONTEXT
                        AuthCommand.help(player); // DISPLAY HELP
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

                if (this.failedAttempts.containsKey(playerUUID)) { // FAILED ATTEMPS MAP CLEAR
                        this.failedAttempts.remove(playerUUID);
                        MainLuckSecure.LOGGER.info(player.getName() + " failed authentification attempts cleared.");
                }
        }

        public boolean doesUserHaveGroupWithAuthContext(ProxiedPlayer player) { // CHECK IF SOME OF USER'S GROUP OR PERMISSION NEED CONTEXT AUTHENTICATED
                LuckPerms luckPerms = this.plugin.getLuckPerms();
                return false; // TODO : CHECK IF SOME OF USER'S GROUP OR PERMISSION NEED CONTEXT AUTHENTICATED
        }

        public boolean doesUserHasSetupA2F(ProxiedPlayer player) { // CHECK IF BDD CONTAINS TOTP USER KEY
                SQLiteJDBC jdbc = this.plugin.getJdbc();
                return false; // TODO : CHECK IF BDD CONTAINS TOTP USER KEY
        }

        public String generateTOTPKey(ProxiedPlayer player) { // CREATE A NEW TOTP ENTRY IN BDD & RETURN THE KEY
                SQLiteJDBC jdbc = this.plugin.getJdbc();
                return "key"; // TODO : CREATE A NEW TOTP ENTRY IN BDD & RETURN THE KEY
        }

        /*
         * GETTER
         */
        public List<String> getAuthentificatedUsers() {
            return authentificatedUsers;
        }

        public Map<String, Integer> getFailedAttempts() {
            return failedAttempts;
        }

}
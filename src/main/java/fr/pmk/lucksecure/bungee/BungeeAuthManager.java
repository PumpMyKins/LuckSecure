package fr.pmk.lucksecure.bungee;

import java.util.logging.Logger;

import fr.pmk.lucksecure.AuthManager;
import fr.pmk.lucksecure.data.LuckSecureDatabase;
import net.luckperms.api.LuckPerms;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeeAuthManager extends AuthManager<ProxiedPlayer, BungeePlayer> implements Listener {

        public BungeeAuthManager(Logger logger, LuckSecureDatabase conn, LuckPerms api) {
                super(logger, conn, api);
        }

        @Override
        public BungeePlayer getPlayerAdapter(ProxiedPlayer player) {
                return new BungeePlayer(this.getLuckPerms(), player);
        }

        // Login Event
        @EventHandler
        public void onPlayerPostLoginEvent(PostLoginEvent event) {
                ProxiedPlayer player = event.getPlayer();
                super.onPlayerPostLoginEvent(player);
        }
        
        // Left Event
        @EventHandler
        public void onPlayerDisconnectEvent(PlayerDisconnectEvent event) {
                ProxiedPlayer player = event.getPlayer();
                super.onPlayerDisconnectEvent(player);
        }

}
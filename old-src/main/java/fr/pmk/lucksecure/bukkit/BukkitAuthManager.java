package fr.pmk.lucksecure.bukkit;

import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.pmk.lucksecure.AuthManager;
import fr.pmk.lucksecure.data.LuckSecureDatabase;
import net.luckperms.api.LuckPerms;
import net.md_5.bungee.event.EventHandler;

public class BukkitAuthManager extends AuthManager<Player, BukkitPlayer> implements Listener {

    public BukkitAuthManager(Logger logger, LuckSecureDatabase conn, LuckPerms api) {
        super(logger, conn, api);
    }

    @Override
    public BukkitPlayer getPlayerAdapter(Player player) {
        return new BukkitPlayer(this.getLuckPerms(), player);
    }

    // Login Event
    @EventHandler
    public void onPlayerPostLoginEvent(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        super.onPlayerPostLoginEvent(player);
    }

    // Left Event
    @EventHandler
    public void onPlayerDisconnectEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        super.onPlayerDisconnectEvent(player);
    }

}
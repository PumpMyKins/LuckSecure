package fr.pmk.lucksecure.paper;

import java.io.File;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.pmk.lucksecure.common.LuckSecure;

public class PaperLuckSecure extends LuckSecure implements Listener {

    private Main main;

    public PaperLuckSecure(Main main) {
        super(main.getLogger());
        this.main = main;
    }

    @Override
    protected File getPluginDataPath() {
        return this.main.getDataFolder();
    }

    @Override
    protected boolean isServerBehindProxy() {
        return this.main.getServer().spigot().getSpigotConfig().getBoolean("settings.bungeecord") || this.main.getServer().spigot().getPaperConfig().getBoolean("proxies.velocity.enabled");
    }

    @Override
    protected void registerListeners() {
        this.main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void onPlayerLoginEvent(PlayerLoginEvent event) {
        this.manager.onPlayerPostLoginEvent(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLoginEvent(PlayerQuitEvent event) {
        this.manager.onPlayerDisconnectEvent(event.getPlayer());
    }

    @Override
    protected void registerCommands() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'registerCommands'");
    } 

}
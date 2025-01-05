package fr.pmk.lucksecure.paper;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import fr.pmk.lucksecure.common.AuthContextCalculator;
import fr.pmk.lucksecure.common.LuckSecure;
import fr.pmk.lucksecure.paper.command.PAuthCommand;
import fr.pmk.lucksecure.paper.command.PResetAuthCommand;
import fr.pmk.lucksecure.paper.command.PStatusAuthCommand;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.audience.Audience;

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
        LifecycleEventManager<Plugin> manager = this.main.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register("lsauth", "LuckSecure Authentication Command", new PAuthCommand(this));
            commands.register("lsauth-reset", "LuckSecure Reset Player TOTP Secret Command", new PResetAuthCommand(this));
            commands.register("lsauth-status", "LuckSecure Display Player TOTP Status Command", new PStatusAuthCommand(this));
        });
    }

    @Override
    public Audience getPlayer(String name) {
        return this.main.getServer().getPlayer(name);
    }

    @Override
    protected AuthContextCalculator<Player> getLuckPermsContextCalculator() {
        return new PAuthContextCalculator(this.manager);
    } 

}
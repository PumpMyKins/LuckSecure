package fr.pmk.lucksecure.velocity;

import java.io.File;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;

import fr.pmk.lucksecure.common.AuthContextCalculator;
import fr.pmk.lucksecure.common.LuckSecure;
import fr.pmk.lucksecure.velocity.command.VAuthCommand;
import fr.pmk.lucksecure.velocity.command.VResetAuthCommand;
import fr.pmk.lucksecure.velocity.command.VStatusAuthCommand;
import net.kyori.adventure.audience.Audience;

public class VelocityLuckSecure extends LuckSecure {

    private Main main;

    public VelocityLuckSecure(Main main) {
        super(main.getLogger());
        this.main = main;
    }

    @Override
    protected File getPluginDataPath() {
        return this.main.getDataDirectory().toFile();
    }

    @Override
    protected boolean isServerBehindProxy() {
        return false;
    }

    @Override
    protected void registerListeners() {
        this.main.getServer().getEventManager().register(this.main, LoginEvent.class, event -> manager.onPlayerPostLoginEvent(event.getPlayer()));
        this.main.getServer().getEventManager().register(this.main, DisconnectEvent.class, event -> manager.onPlayerDisconnectEvent(event.getPlayer()));
    }

    @Override
    protected void registerCommands() {
        CommandManager commandManager = this.main.getServer().getCommandManager();

        CommandMeta commandMetaAuth = commandManager.metaBuilder("lsauth").plugin(this.main).build();
        commandManager.register(commandMetaAuth, new VAuthCommand(this));

        CommandMeta commandMetaReset = commandManager.metaBuilder("lsauth-reset").plugin(this.main).build();
        commandManager.register(commandMetaReset, new VResetAuthCommand(this));

        CommandMeta commandMetaStatus = commandManager.metaBuilder("lsauth-status").plugin(this.main).build();
        commandManager.register(commandMetaStatus, new VStatusAuthCommand(this));
    }

    @Override
    public Audience getPlayer(String name) {
        return this.main.getServer().getPlayer(name).orElse(null);
    }

    @Override
    protected AuthContextCalculator<?> getLuckPermsContextCalculator() {
        return new VAuthContextCalculator(this.manager);
    }

}
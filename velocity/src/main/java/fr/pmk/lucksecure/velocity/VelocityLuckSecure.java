package fr.pmk.lucksecure.velocity;

import java.io.File;

import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;

import fr.pmk.lucksecure.common.LuckSecure;

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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'registerCommands'");
    }

}
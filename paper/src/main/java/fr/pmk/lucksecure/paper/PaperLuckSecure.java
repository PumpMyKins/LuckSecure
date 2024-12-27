package fr.pmk.lucksecure.paper;

import java.io.File;

import fr.pmk.lucksecure.common.LuckSecure;

public class PaperLuckSecure extends LuckSecure {

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
    protected void registerCommands() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'registerCommands'");
    }

    @Override
    protected void registerListeners() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'registerListeners'");
    }  

}
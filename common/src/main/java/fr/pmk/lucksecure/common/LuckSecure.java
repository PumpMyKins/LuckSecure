package fr.pmk.lucksecure.common;

import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

public abstract class LuckSecure {

    protected LuckPerms luckPerms;

    public final void init() throws Exception {
        try {
            this.luckPerms = LuckPermsProvider.get();
        } catch (IllegalStateException e) {
            throw new Exception("LuckPerms API not loaded !");
        }

        saveDefaultConfigs();
        YamlConfiguration config = getPluginConfiguration();

        boolean syncAuthUsers = config.getBoolean("syncAuthUsers", false);
        // if behindProxy or sync auth users enabled -> use redis for session
        if (syncAuthUsers || isServerBehindProxy()) {
            // JedisAuthManager
        } else {
            // AuthManager
        }

        if (!isServerBehindProxy()) {
            registerCommands();
            registerListeners();
        }

    }

    protected abstract Logger getLogger();

    protected abstract boolean isServerBehindProxy();


    protected abstract void saveDefaultConfigs();
    protected abstract YamlConfiguration getPluginConfiguration();

    protected abstract void registerCommands();
    protected abstract void registerListeners();
}

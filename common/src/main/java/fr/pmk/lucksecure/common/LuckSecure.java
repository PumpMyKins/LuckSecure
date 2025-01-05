package fr.pmk.lucksecure.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

import org.apache.commons.configuration2.YAMLConfiguration;

import fr.pmk.lucksecure.common.database.LuckSecureDatabase;
import net.kyori.adventure.audience.Audience;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import redis.clients.jedis.JedisPooled;

public abstract class LuckSecure {

    public static final String LUCKSECURE_CONFIG = "config.yml";

    public static final String LUCKSECURE_BASE_MSG = "<b>[</b><aqua>Luck</aqua><dark_aqua>Secure</dark_aqua><b>]</b> > ";
    public static final String COMMAND_USAGE_BASE_MSG = LUCKSECURE_BASE_MSG + "<aqua>Use : </aqua>";
    public static final String UNHANDLED_EXCEPTION_MSG = LUCKSECURE_BASE_MSG + "<red>Unhandled exception... contact server admin.</red>";

    private Logger logger;
    protected LuckPerms luckPerms;
    protected YAMLConfiguration config;
    protected AuthManager manager;

    protected LuckSecure(Logger logger) {
        this.logger = logger;
    }

    protected final Logger getLogger() {
        return this.logger;
    }

    public final AuthManager getAuthManager() {
        return manager;
    }

    public final YAMLConfiguration getConfig() {
        return config;
    }

    protected abstract File getPluginDataPath();
    private void saveDefaultConfigs() throws IOException {
        File dataPath = getPluginDataPath();
        if (!dataPath.exists()) {
            dataPath.mkdirs();
        }

        File target = new File(dataPath, LUCKSECURE_CONFIG);
        if (!target.exists()) {
            InputStream defaultConfig = getClass().getResourceAsStream("/" + LUCKSECURE_CONFIG);
            Files.copy(defaultConfig, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public final void init() throws Exception {
        try {
            this.luckPerms = LuckPermsProvider.get();
        } catch (IllegalStateException e) {
            throw new Exception("LuckPerms API not loaded !");
        }

        try {
            saveDefaultConfigs();
            config = new YAMLConfiguration();
            config.read(new FileInputStream(new File(getPluginDataPath(), LUCKSECURE_CONFIG)));
        } catch (IllegalStateException e) {
            throw new Exception("Unable to initialize LuckSecure config.", e);
        }

        LuckSecureDatabase database = null;
        if (isServerBehindProxy()) {
            getLogger().warning("LuckSecure running behind Minecraft Proxy. No local storage active. Redis configuration required.");
        } else {
            // LuckSecure running on Proxy or on paper without bungeecord/velocity option enabled
            File db = new File(getPluginDataPath(), "lucksecure");
            database = new LuckSecureDatabase(getLogger(), "jdbc:h2:./" + db.toPath());
        }

        boolean syncAuthUsers = config.getBoolean("syncAuthUsers", false);
        // if behindProxy or sync auth users enabled -> use redis for session
        if (syncAuthUsers || isServerBehindProxy()) {
            logger.info("init Redis connection.");
            JedisPooled jedis = JedisAuthManager.getJedisFromConfig(config);
            manager = new JedisAuthManager(getLogger(), database, luckPerms, jedis);
        } else {
            // AuthManager: no Redis, Local H2 Storage
            manager = new AuthManager(getLogger(), database, luckPerms);
        }

        logger.info("Registering LuckPerms context calculator.");
        AuthContextCalculator<?> contextCalculator = getLuckPermsContextCalculator();
        luckPerms.getContextManager().registerCalculator(contextCalculator);

        if (!isServerBehindProxy()) {
            registerListeners();
            registerCommands();
        }

    }

    public final void close() {
        if (this.manager instanceof JedisAuthManager m) {
            m.closeJedis();
        }
    }

    protected abstract boolean isServerBehindProxy();

    protected abstract AuthContextCalculator<?> getLuckPermsContextCalculator();
    protected abstract void registerCommands();
    protected abstract void registerListeners();

    public abstract Audience getPlayer(String name);
}

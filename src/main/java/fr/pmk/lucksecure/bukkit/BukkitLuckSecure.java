package fr.pmk.lucksecure.bukkit;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import fr.pmk.lucksecure.bukkit.command.BAuthCommand;
import fr.pmk.lucksecure.bukkit.command.BResetAuthCommand;
import fr.pmk.lucksecure.bukkit.command.BStatusAuthCommand;
import fr.pmk.lucksecure.data.LuckSecureDatabase;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

public class BukkitLuckSecure extends JavaPlugin {

    // LOGGER
    public static Logger LOGGER;

    private LuckPerms luckPerms;
    private LuckSecureDatabase bdd;

    @Override
    public void onEnable() {
        // SET LOGGER
        LOGGER = this.getLogger();
        // RETREIVE LUCKPERMS API
        try {
            this.luckPerms = LuckPermsProvider.get();
        } catch (IllegalStateException e) {
            LOGGER.severe("LuckPerms API not loaded !");
            this.onDisable();
            return;
        }

        // CONFIG FOLDER
        if (!getDataFolder().exists()) {
            LOGGER.info("Created config folder: " + getDataFolder().mkdir());
         }

        // INIT SQLite DB
        try {
            File db = new File(getDataFolder(), "lucksecure.db");
            this.bdd = new LuckSecureDatabase(LOGGER, "jdbc:h2:./" + db.toPath());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            this.onDisable();
            return;
        }

        // INIT PLUGIN
        BukkitAuthManager manager = new BukkitAuthManager(LOGGER, bdd, luckPerms);
        manager.setupLuckPermsContext();

        this.getServer().getPluginManager().registerEvents(manager, this);

        // REGISTER AUTH COMMAND
        this.getCommand("kit").setExecutor(new BAuthCommand(this, manager));
        this.getCommand("kit").setExecutor(new BResetAuthCommand(this, manager));
        this.getCommand("kit").setExecutor(new BStatusAuthCommand(this, manager));

        LOGGER.info("Successfully enabled.");
    }

    @Override
    public void onDisable() {
        LOGGER.info("disabled.");
        if (bdd != null) {
            try {
                bdd.close();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        super.onDisable();
    }

    /*
     * GETTER
     */
    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public LuckSecureDatabase getJdbc() {
        return bdd;
    }

}

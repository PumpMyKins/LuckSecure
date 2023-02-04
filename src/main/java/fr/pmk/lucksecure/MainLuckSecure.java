package fr.pmk.lucksecure;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.plugin.Plugin;

public class MainLuckSecure extends Plugin {
    // LOGGER
    protected static Logger LOGGER;

    private LuckPerms luckPerms;
    private SQLiteJDBC bdd;

    @Override
    public void onEnable() {
        // SET LOGGER
        LOGGER = this.getLogger();
        // RETREIVE LUCKPERMS API
        this.luckPerms = LuckPermsProvider.get();

        // CONFIG FOLDER
        if (!getDataFolder().exists()) {
            LOGGER.info("Created config folder: " + getDataFolder().mkdir());
         }

        // INIT SQLite DB
        try {
            File db = new File(getDataFolder(), "totp.db");
            this.bdd = new SQLiteJDBC("jdbc:sqlite:" + db.toPath());
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return;
        }

        // INIT PLUGIN
        AuthManager manager = new AuthManager(this);
        AuthContextCalculator calculator = new AuthContextCalculator(manager);

        luckPerms.getContextManager().registerCalculator(calculator); // REGISTER A2F Context

        this.getProxy().getPluginManager().registerCommand(this, new AuthCommand(manager)); // REGISTER AUTH COMMAND
        this.getProxy().getPluginManager().registerCommand(this, new ResetAuthCommand(this, manager)); // REGISTER AUTH COMMAND

        LOGGER.info("Successfully enabled.");

    }

    @Override
    public void onDisable() {
        if (bdd != null) {
            try {
                bdd.close();
            } catch (SQLException e) {
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

    public SQLiteJDBC getJdbc() {
        return bdd;
    }

}

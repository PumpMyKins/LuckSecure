package fr.pmk;

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

        // INIT SQLite DB
        try {
            this.bdd = new SQLiteJDBC("jdbc:sqlite:" + getDataFolder() + "totp.db");
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return;
        }

        // INIT PLUGIN
        AuthManager manager = new AuthManager(this);
        AuthContextCalculator calculator = new AuthContextCalculator(manager);

        luckPerms.getContextManager().registerCalculator(calculator); // REGISTER A2F Context

        this.getProxy().getPluginManager().registerCommand(this, new AuthCommand(manager)); // REGISTER AUTH COMMAND

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

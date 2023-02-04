package fr.pmk.lucksecure;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.pmk.lucksecure.command.AuthCommand;
import fr.pmk.lucksecure.command.ResetAuthCommand;
import fr.pmk.lucksecure.command.StatusAuthCommand;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Plugin;

public class MainLuckSecure extends Plugin {

    // TEXT COMPONENT
    public final static BaseComponent[] LUCKSECURE_BASE_COMPONENTS = new ComponentBuilder().append("[").color(ChatColor.WHITE).bold(true).append("Luck").bold(false).color(ChatColor.AQUA).append("Secure").color(ChatColor.DARK_AQUA).append("]").color(ChatColor.WHITE).bold(true).append(" > ").bold(false).color(ChatColor.DARK_AQUA).create();
    public final static BaseComponent[] COMMAND_USAGE_BASE_COMPONENTS = new ComponentBuilder().append(LUCKSECURE_BASE_COMPONENTS).append("Use : ").color(ChatColor.AQUA).create();
    public final static BaseComponent[] UNHANDLED_EXCEPTION_BASE_COMPONENTS = new ComponentBuilder().append(LUCKSECURE_BASE_COMPONENTS).append("Unhandled exception... contact server admin.").color(ChatColor.RED).create();

    // LOGGER
    public static Logger LOGGER;

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

        luckPerms.getContextManager().registerCalculator(calculator); // REGISTER 2AF Context

        this.getProxy().getPluginManager().registerCommand(this, new AuthCommand(manager)); // REGISTER AUTH COMMAND
        this.getProxy().getPluginManager().registerCommand(this, new ResetAuthCommand(this, manager)); // REGISTER AUTH COMMAND
        this.getProxy().getPluginManager().registerCommand(this, new StatusAuthCommand(this, manager)); // REGISTER AUTH COMMAND

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

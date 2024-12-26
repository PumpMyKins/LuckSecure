package fr.pmk.lucksecure.command;

import java.sql.SQLException;
import java.util.logging.Level;

import fr.pmk.lucksecure.AuthManager;
import fr.pmk.lucksecure.MainLuckSecure;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ResetAuthCommand extends Command {

    private MainLuckSecure main;
    private AuthManager manager;

    public ResetAuthCommand(MainLuckSecure main, AuthManager manager) {
        super("lsauth-reset", "lsauth.cmd");
        this.main = main;
        this.manager = manager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ((sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent("Console command only"));
            return;
        }

        if (args.length == 1) {
            String playerName = args[0];
            ProxiedPlayer player = main.getProxy().getPlayer(playerName);
            if (player != null) {
                playerName = player.getUniqueId().toString();
            }

            if (playerName.trim().isEmpty()) {
                help(sender);
                return;
            }

            try {
                if (manager.deleteUserToken2AF(playerName)) {
                    sender.sendMessage(new ComponentBuilder().append(MainLuckSecure.LUCKSECURE_BASE_COMPONENTS).append("2AF setup reset succeed for ").color(ChatColor.AQUA).append(playerName).color(ChatColor.WHITE).append(".").color(ChatColor.AQUA).create());
                } else {
                    sender.sendMessage(new ComponentBuilder().append(MainLuckSecure.LUCKSECURE_BASE_COMPONENTS).append("No 2AF setup found for this player name or UUID.").color(ChatColor.RED).create());

                }
            } catch (SQLException e) {
                sender.sendMessage(MainLuckSecure.UNHANDLED_EXCEPTION_BASE_COMPONENTS);
                MainLuckSecure.LOGGER.severe("Exception on user 2AF setup deleting !");
                MainLuckSecure.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }

        }else {
            help(sender);
        }     
        
    }

    public static void help(CommandSender sender) {
        sender.sendMessage(new ComponentBuilder().append(MainLuckSecure.LUCKSECURE_BASE_COMPONENTS).append("Reset the 2AF setup of a player").color(ChatColor.RED).create());
        sender.sendMessage(new ComponentBuilder().append(MainLuckSecure.COMMAND_USAGE_BASE_COMPONENTS).append("/lsauth-reset {player/uuid}").color(ChatColor.GREEN).create());
    }
    
}

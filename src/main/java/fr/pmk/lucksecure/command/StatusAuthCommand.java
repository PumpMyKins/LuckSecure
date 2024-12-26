package fr.pmk.lucksecure.command;

import fr.pmk.lucksecure.AuthManager;
import fr.pmk.lucksecure.MainLuckSecure;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class StatusAuthCommand extends Command {

    private MainLuckSecure main;
    private AuthManager manager;

    public StatusAuthCommand(MainLuckSecure main, AuthManager manager) {
        super("lsauth-status", "lsauth.cmd");
        this.main = main;
        this.manager = manager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            String playerName = args[0];

            if (playerName.trim().isEmpty()) {
                help(sender);
                return;
            }

            ProxiedPlayer player = main.getProxy().getPlayer(playerName);
            if (player == null) {
                sender.sendMessage(new ComponentBuilder().append(MainLuckSecure.LUCKSECURE_BASE_COMPONENTS).append(playerName).color(ChatColor.WHITE).append(" player not found.").color(ChatColor.RED).create());
                return;
            }

            if (manager.getAuthentificatedUsers().contains(player.getUniqueId().toString())) {
                sender.sendMessage(new ComponentBuilder().append(MainLuckSecure.LUCKSECURE_BASE_COMPONENTS).append(playerName).color(ChatColor.WHITE).append(" 2AF OK.").color(ChatColor.AQUA).create());
            } else {
                sender.sendMessage(new ComponentBuilder().append(MainLuckSecure.LUCKSECURE_BASE_COMPONENTS).append(playerName).color(ChatColor.WHITE).append(" 2AF NOK.").color(ChatColor.RED).create());
            }

        }else {
            help(sender);
        }     
        
    }

    public static void help(CommandSender sender) {
        sender.sendMessage(new ComponentBuilder().append(MainLuckSecure.LUCKSECURE_BASE_COMPONENTS).append("Check the 2AF status of a connected player").color(ChatColor.RED).create());
        sender.sendMessage(new ComponentBuilder().append(MainLuckSecure.COMMAND_USAGE_BASE_COMPONENTS).append("/lsauth-status {player}").color(ChatColor.GREEN).create());
    }
    
}

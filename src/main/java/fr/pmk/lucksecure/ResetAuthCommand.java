package fr.pmk.lucksecure;

import java.sql.SQLException;
import java.util.logging.Level;

import net.md_5.bungee.api.CommandSender;
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
                if (manager.deleteUserTokenA2F(playerName)) {
                    sender.sendMessage(new TextComponent("§l§f[§r§bLuck§3Secure§l§f]§r §3> §bA2F reset succeed."));
                } else {
                    sender.sendMessage(new TextComponent("§l§f[§r§bLuck§3Secure§l§f]§r §3> §cNo A2F found for this player name or UUID."));
                }
            } catch (SQLException e) {
                sender.sendMessage(new TextComponent("§l§f[§r§bLuck§3Secure§l§f]§r §3> §cUnhandled exception... contact server admin."));
                MainLuckSecure.LOGGER.severe("Exception on user A2F setup verification !");
                MainLuckSecure.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }

        }else {
            help(sender);
        }     
        
    }

    public static void help(CommandSender sender) {
        sender.sendMessage(new TextComponent("§l§f[§r§bLuck§3Secure§l§f]§r §3> §cReset the A2F of a player"));
        sender.sendMessage(new TextComponent("§l§f[§r§bLuck§3Secure§l§f]§r §3> §bUse : §a/lsauth-reset {player/uuid}"));
    }
    
}

package fr.pmk.lucksecure.command;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.logging.Level;

import fr.pmk.lucksecure.AuthManager;
import fr.pmk.lucksecure.MainLuckSecure;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class AuthCommand extends Command {

    private AuthManager manager;

    public AuthCommand(AuthManager manager) {
        super("lsauth", "");
        this.manager = manager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent("User command only"));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (!manager.doesUserHaveGroupWithAuthContext(player)) {
            return;
        }

        // CHECK IF THE PLAYER IS ALREADY AUTHENTICATED && IF USER HAS GROUP OR PERMISSION WHICH NEED AUTHENTICATED CONTEXT
        if (!manager.getAuthentificatedUsers().contains(player.getUniqueId().toString())) {
            try {
                if (manager.doesUserHasSetup2AF(player)) { // CHECK IF 2AF IS ALREADY SETUP
                    if (args.length == 1 && args[0].matches("[0-9]+")) {
                        String code = args[0];
                        try {
                            if (manager.isUserToken2AFValid(player, code)) {
                                player.sendMessage(new ComponentBuilder().append(MainLuckSecure.LUCKSECURE_BASE_COMPONENTS).append("Authentication succeed !").color(ChatColor.AQUA).create());
                            } else {
                                player.sendMessage(new ComponentBuilder().append(MainLuckSecure.LUCKSECURE_BASE_COMPONENTS).append("Authentication failed...").color(ChatColor.RED).create());
                                help(player);
                            }
                        } catch (InvalidKeyException | IllegalArgumentException | NoSuchAlgorithmException | IOException e) {
                            player.sendMessage(MainLuckSecure.UNHANDLED_EXCEPTION_BASE_COMPONENTS);
                            MainLuckSecure.LOGGER.severe("Exception on user 2AF code verification !");
                            MainLuckSecure.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        }
                    } else { // INVALID COMMAND FORMAT
                        help(player);
                    }
                } else {
                    String key = manager.generateUserToken2AF(player);
                    String url = AuthManager.generateUserTokenUrl(player.getDisplayName(), key, player.getDisplayName() + " - LuckSecure@" + ProxyServer.getInstance().getName());
                    BaseComponent[] urlTextComponent = new ComponentBuilder().append(MainLuckSecure.LUCKSECURE_BASE_COMPONENTS).append("Import the key:").color(ChatColor.AQUA)
                    .append(key).bold(true)
                    .append(" or scan the ").color(ChatColor.AQUA)
                    .append("QRCODE").bold(true).event(new ClickEvent(Action.OPEN_URL, url))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder().append("Click to open QRCODE").bold(true).create()))
                    .append(" with your authenticator app.").color(ChatColor.AQUA).create();

                    player.sendMessage(urlTextComponent);
                    player.sendMessage(new ComponentBuilder().append(MainLuckSecure.LUCKSECURE_BASE_COMPONENTS).append("Please authenticate yourself to retreive all your access !").color(ChatColor.RED).create());
                }
            } catch (SQLException e) {
                player.sendMessage(MainLuckSecure.UNHANDLED_EXCEPTION_BASE_COMPONENTS);
                MainLuckSecure.LOGGER.severe("Exception on user 2AF setup verification !");
                MainLuckSecure.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        } else {
            player.sendMessage(new ComponentBuilder().append(MainLuckSecure.LUCKSECURE_BASE_COMPONENTS).append("You don't need to authenticate yourself !").color(ChatColor.RED).create());
        }
        
    }

    public static void help(ProxiedPlayer player) {
        player.sendMessage(new ComponentBuilder().append(MainLuckSecure.LUCKSECURE_BASE_COMPONENTS).append("You need to authenticate yourself to retreive all your access !").color(ChatColor.RED).create());
        player.sendMessage(new ComponentBuilder().append(MainLuckSecure.COMMAND_USAGE_BASE_COMPONENTS).append("/lsauth {code}").color(ChatColor.GREEN).create());
    }
    
}

package fr.pmk.lucksecure;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.logging.Level;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class AuthCommand extends Command {

    private AuthManager manager;

    public AuthCommand(AuthManager manager) {
        super("lsauth", "lsauth.cmd");
        this.manager = manager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent("User command only"));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        // CHECK IF THE PLAYER IS ALREADY AUTHENTICATED && IF USER HAS GROUP OR PERMISSION WHICH NEED AUTHENTICATED CONTEXT
        if (!manager.getAuthentificatedUsers().contains(player.getUniqueId().toString()) && manager.doesUserHaveGroupWithAuthContext(player)) {
            try {
                if (manager.doesUserHasSetupA2F(player)) { // CHECK IF A2F IS ALREADY SETUP
                    if (args.length == 1 && args[0].matches("[0-9]+")) {
                        String code = args[0];
                        try {
                            if (manager.isUserTokenA2FValid(player, code)) {
                                player.sendMessage(new TextComponent("§l§f[§r§bLuck§3Secure§l§f]§r §3> §bAuthentication succed !"));
                            } else {
                                player.sendMessage(new TextComponent("§l§f[§r§bLuck§3Secure§l§f]§r §3> §cAuthentication failed..."));
                                help(player);
                            }
                        } catch (InvalidKeyException | IllegalArgumentException | NoSuchAlgorithmException | IOException e) {
                            player.sendMessage(new TextComponent("§l§f[§r§bLuck§3Secure§l§f]§r §3> §cUnhandled exception... contact server admin."));
                            MainLuckSecure.LOGGER.severe("Exception on user code verification !");
                            MainLuckSecure.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        }
                    } else { // INVALID COMMAND FORMAT
                        help(player);
                    }
                } else {
                    String key = manager.generateUserTokenA2F(player);
                    String url = AuthManager.generateUserTokenUrl(player.getDisplayName(), key, player.getDisplayName() + " - LuckSecure@" + ProxyServer.getInstance().getName());
                    BaseComponent[] urlTextComponent = new ComponentBuilder().append("§l§f[§r§bLuck§3Secure§l§f]§r §3> §bImport the key:§r")
                    .append(key).bold(true)
                    .append("§r§b or scan the §r")
                    .append("QRCODE").bold(true).event(new ClickEvent(Action.OPEN_URL, url))
                    .append("§r§b with your authenticator app.").create();
                    player.sendMessage(urlTextComponent);
                    player.sendMessage(new TextComponent("§l§f[§r§bLuck§3Secure§l§f]§r §3> §cPlease authenticate yourself to retreive all your access !"));
                }
            } catch (SQLException e) {
                player.sendMessage(new TextComponent("§l§f[§r§bLuck§3Secure§l§f]§r §3> §cUnhandled exception... contact server admin."));
                MainLuckSecure.LOGGER.severe("Exception on user A2F setup verification !");
                MainLuckSecure.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        } else {
            player.sendMessage(new TextComponent("§l§f[§r§bLuck§3Secure§l§f]§r §3> §cYou don't need to authenticate yourself !"));
        }
        
    }

    public static void help(ProxiedPlayer player) {
        player.sendMessage(new TextComponent("§l§f[§r§bLuck§3Secure§l§f]§r §3> §cYou need to authenticate yourself to retreive all your access !"));
        player.sendMessage(new TextComponent("§l§f[§r§bLuck§3Secure§l§f]§r §3> §bUse : §a/lsauth {code}"));
    }
    
}

package fr.pmk;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
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
        ProxiedPlayer player = (ProxiedPlayer) sender;
        // CHECK IF THE PLAYER IS ALREADY AUTHENTICATED && IF USER HAS GROUP OR PERMISSION WHICH NEED AUTHENTICATED CONTEXT
        if (!manager.getAuthentificatedUsers().contains(player.getUniqueId().toString()) && manager.doesUserHaveGroupWithAuthContext(player)) {
            if (manager.doesUserHasSetupA2F(player)) { // CHECK IF A2F IS ALREADY SETUP
                // TODO : CHECK COMMAND ARG
            } else {
                String key = manager.generateTOTPKey(player);
                // TODO : DISPLAY QR CODE & KEY
                player.sendMessage(new TextComponent("§l§f[§r§bLuck§3Secure§l§f]§r §3> §cThank's to authenticate yourself to retreive all your access !"));
            }
        } else {
            player.sendMessage(new TextComponent("§l§f[§r§bLuck§3Secure§l§f]§r §3> §cYou don't need to authenticate yourself !"));
        }
        
    }

    public static void help(ProxiedPlayer player) {
        player.sendMessage(new TextComponent("§l§f[§r§bLuck§3Secure§l§f]§r §3> §cYou need to authenticate yourself to retreive all your access !"));
        player.sendMessage(new TextComponent("§l§f[§r§bLuck§3Secure§l§f]§r §3> §bUse : §a/lsauth"));
    }
    
}

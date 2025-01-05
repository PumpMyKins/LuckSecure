package fr.pmk.lucksecure.common.command;

import java.sql.SQLException;
import java.util.logging.Level;

import fr.pmk.lucksecure.common.AuthManager;
import fr.pmk.lucksecure.common.LuckSecure;
import fr.pmk.lucksecure.common.Util;
import net.kyori.adventure.audience.Audience;

public class StatusAuthCommand {
    
    private LuckSecure luckSecure;
    private AuthManager manager;

    public StatusAuthCommand(LuckSecure luckSecure) {
        this.luckSecure = luckSecure;
        this.manager = this.luckSecure.getAuthManager();
    }

    public String permission() {
        return "lsauth.cmd";
    }

    public void execute(Audience sender, String[] args) {
        if (args.length == 1) {
            String playerName = args[0];

            if (playerName.trim().isEmpty()) {
                help(sender);
                return;
            }

            Audience target = this.luckSecure.getPlayer(playerName);

            if (target == null) {
                sender.sendMessage(Util.mm(LuckSecure.LUCKSECURE_BASE_MSG + "<white>" + playerName + "</white> <red>player not found.</red>"));
                return;
            }

            if (manager.doesUserHavePermWithAuthContext(target)) {
                sender.sendMessage(Util.mm(LuckSecure.LUCKSECURE_BASE_MSG + "<white>" + playerName + "</white> <red>TOTP Authentication required.</red>"));
                try {
                    if (manager.doesUserHaveTotpSetup(target)) {
                        sender.sendMessage(Util.mm(LuckSecure.LUCKSECURE_BASE_MSG + "<white>" + playerName + "</white> <aqua>TOTP Setup OK.</aqua>"));
                    } else {
                        sender.sendMessage(Util.mm(LuckSecure.LUCKSECURE_BASE_MSG + "<white>" + playerName + "</white> <red>TOTP Setup NOK.</red>"));
                    }
                } catch (SQLException e) {
                    sender.sendMessage(Util.mm(LuckSecure.UNHANDLED_EXCEPTION_MSG));
                    this.manager.getLogger().severe("Exception on user TOTP verification !");
                    this.manager.getLogger().log(Level.SEVERE, e.getMessage(), e);
                }
            } else {
                sender.sendMessage(Util.mm(LuckSecure.LUCKSECURE_BASE_MSG + "<white>" + playerName + "</white> <aqua>TOTP Authentication not required.</aqua>"));
            }
            
            if (manager.isAuthenticated(target)) {
                sender.sendMessage(Util.mm(LuckSecure.LUCKSECURE_BASE_MSG + "<white>" + playerName + "</white> <aqua>TOTP Authentication OK.</aqua>"));
            } else {
                sender.sendMessage(Util.mm(LuckSecure.LUCKSECURE_BASE_MSG + "<white>" + playerName + "</white> <red>TOTP Authentication NOK.</red>"));
            }
        }else {
            help(sender);
        }     
    }

    private void help(Audience sender) {
        sender.sendMessage(Util.mm(LuckSecure.LUCKSECURE_BASE_MSG + "<red>Check the AUTH status of a player.</red>"));
        sender.sendMessage(Util.mm(LuckSecure.COMMAND_USAGE_BASE_MSG + "<green>/lsauth-status {player}</green>"));
    }
    
}

package fr.pmk.lucksecure.command;

import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.pmk.lucksecure.AuthManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class StatusAuthCommand<S, P extends S, M extends AuthManager<P, ?>, A extends ICommandAdapter<S, P>> {
    
    private Logger logger;
    private M manager;
    private A adapter;

    public StatusAuthCommand(Logger logger, M manager, A adapter) {
        this.logger = logger;
        this.manager = manager;
        this.adapter = adapter;
    }

    public boolean execute(S sender, String[] args) {
        if (args.length == 1) {
            String playerName = args[0];

            if (playerName.trim().isEmpty()) {
                help(sender);
                return false;
            }

            P player = this.adapter.getServerPlayer(playerName);
            if (player == null) {
                this.adapter.sendMessageToSender(sender, new ComponentBuilder().append(AuthCommand.LUCKSECURE_BASE_COMPONENTS).append(playerName).color(ChatColor.WHITE).append(" player not found.").color(ChatColor.RED).create());
                return false;
            }

            UUID uuid = this.manager.getPlayerAdapter(player).getUniqueId();

            if (manager.doesUserHavePermWithAuthContext(player)) {
                this.adapter.sendMessageToSender(sender, new ComponentBuilder().append(AuthCommand.LUCKSECURE_BASE_COMPONENTS).append(playerName).color(ChatColor.WHITE).append(" TOTP Authentication required.").color(ChatColor.RED).create());
                try {
                    if (manager.doesUserHaveTotpSetup(player)) {
                        this.adapter.sendMessageToSender(sender, new ComponentBuilder().append(AuthCommand.LUCKSECURE_BASE_COMPONENTS).append(playerName).color(ChatColor.WHITE).append(" TOTP Setup OK.").color(ChatColor.AQUA).create());
                    } else {
                        this.adapter.sendMessageToSender(sender, new ComponentBuilder().append(AuthCommand.LUCKSECURE_BASE_COMPONENTS).append(playerName).color(ChatColor.WHITE).append(" TOTP Setup NOK.").color(ChatColor.RED).create());
                    }
                } catch (SQLException e) {
                    this.adapter.sendMessageToSender(sender, AuthCommand.UNHANDLED_EXCEPTION_BASE_COMPONENTS);
                    this.logger.severe("Exception on user 2AF setup deleting !");
                    this.logger.log(Level.SEVERE, e.getMessage(), e);
                    return false;
                }
            } else {
                this.adapter.sendMessageToSender(sender, new ComponentBuilder().append(AuthCommand.LUCKSECURE_BASE_COMPONENTS).append(playerName).color(ChatColor.WHITE).append(" TOTP Authentication not required.").color(ChatColor.AQUA).create());
            }
            
            if (manager.getAuthentificatedUsers().contains(uuid)) {
                this.adapter.sendMessageToSender(sender, new ComponentBuilder().append(AuthCommand.LUCKSECURE_BASE_COMPONENTS).append(playerName).color(ChatColor.WHITE).append(" TOTP Authentication OK.").color(ChatColor.AQUA).create());
            } else {
                this.adapter.sendMessageToSender(sender, new ComponentBuilder().append(AuthCommand.LUCKSECURE_BASE_COMPONENTS).append(playerName).color(ChatColor.WHITE).append(" TOTP Authentication NOK.").color(ChatColor.RED).create());
            }
            return true;
        }else {
            help(sender);
        } 
        
        return false;        
    }

    private void help(S sender) {
        this.adapter.sendMessageToSender(sender, new ComponentBuilder().append(AuthCommand.LUCKSECURE_BASE_COMPONENTS).append("Check the AUTH status of a connected player").color(ChatColor.RED).create());
        this.adapter.sendMessageToSender(sender, new ComponentBuilder().append(AuthCommand.COMMAND_USAGE_BASE_COMPONENTS).append("/lsauth-status {player}").color(ChatColor.GREEN).create());
    }
    
}

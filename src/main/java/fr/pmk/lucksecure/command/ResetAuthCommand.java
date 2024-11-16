package fr.pmk.lucksecure.command;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.pmk.lucksecure.AuthManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

public final class ResetAuthCommand<S, P extends S, M extends AuthManager<P, ?>, A extends ICommandAdapter<S, P>> {

    private Logger logger;
    private M manager;
    private A adapter;

    public ResetAuthCommand(Logger logger, M manager, A adapter) {
        this.logger = logger;
        this.manager = manager;
        this.adapter = adapter;
    }

    public boolean execute(S sender, String[] args) {
        if (this.adapter.isSenderInstanceOfPlayer(sender)) {
            this.adapter.sendMessageToSender(sender, new ComponentBuilder("Console command only").create());
            return false;
        }

        if (args.length == 1) {
            String playerName = args[0];
            P player = this.adapter.getServerPlayer(playerName);
            if (player == null) {
                this.adapter.sendMessageToSender(sender, new ComponentBuilder().append(AuthCommand.LUCKSECURE_BASE_COMPONENTS).append(playerName).color(ChatColor.WHITE).append(" player not found.").color(ChatColor.RED).create());
                return false;
            }

            try {
                if (manager.deleteUserTotpSecret(player)) {
                    this.adapter.sendMessageToSender(sender,
                            new ComponentBuilder().append(AuthCommand.LUCKSECURE_BASE_COMPONENTS)
                                    .append("2AF setup reset succeed for ").color(ChatColor.AQUA).append(playerName)
                                    .color(ChatColor.WHITE).append(".").color(ChatColor.AQUA).create());
                    return true;
                } else {
                    this.adapter.sendMessageToSender(sender,
                            new ComponentBuilder().append(AuthCommand.LUCKSECURE_BASE_COMPONENTS)
                                    .append("No 2AF setup found for this player.").color(ChatColor.RED).create());

                }
            } catch (SQLException e) {
                this.adapter.sendMessageToSender(sender, AuthCommand.UNHANDLED_EXCEPTION_BASE_COMPONENTS);
                this.logger.severe("Exception on user 2AF setup deleting !");
                this.logger.log(Level.SEVERE, e.getMessage(), e);
            }

        } else {
            help(sender);
        }

        return false;
    }

    private void help(S sender) {
        this.adapter.sendMessageToSender(sender,
                new ComponentBuilder().append(AuthCommand.LUCKSECURE_BASE_COMPONENTS)
                        .append("Reset the 2AF setup of a player").color(ChatColor.RED).create());
        this.adapter.sendMessageToSender(sender,
                new ComponentBuilder().append(AuthCommand.COMMAND_USAGE_BASE_COMPONENTS)
                        .append("/lsauth-reset {player}").color(ChatColor.GREEN).create());
    }

}

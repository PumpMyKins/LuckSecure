package fr.pmk.lucksecure.common.command;

import java.sql.SQLException;
import java.util.logging.Level;

import fr.pmk.lucksecure.common.AuthManager;
import fr.pmk.lucksecure.common.LuckSecure;
import fr.pmk.lucksecure.common.Util;
import net.kyori.adventure.audience.Audience;

public final class ResetAuthCommand {

    private LuckSecure luckSecure;
    private AuthManager manager;

    public ResetAuthCommand(LuckSecure luckSecure) {
        this.luckSecure = luckSecure;
        this.manager = this.luckSecure.getAuthManager();
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

            try {
                if (manager.deleteUserTotpSecret(target)) {
                    sender.sendMessage(Util.mm(LuckSecure.LUCKSECURE_BASE_MSG + "<aqua>TOTP successfully reset for</aqua> <white>" + playerName + "</white><aqua>.</aqua>"));
                } else {
                    sender.sendMessage(Util.mm(LuckSecure.LUCKSECURE_BASE_MSG + "<red>No TOTP found for this player.</red>"));
                }
            } catch (SQLException e) {
                sender.sendMessage(Util.mm(LuckSecure.UNHANDLED_EXCEPTION_MSG));
                this.manager.getLogger().severe("Exception on user 2AF setup deleting !");
                this.manager.getLogger().log(Level.SEVERE, e.getMessage(), e);
            }
        } else {
            help(sender);
        }
    }

    private void help(Audience sender) {
        sender.sendMessage(Util.mm(LuckSecure.LUCKSECURE_BASE_MSG + "<red>Reset the TOTP of a player.</red>"));
        sender.sendMessage(Util.mm(LuckSecure.COMMAND_USAGE_BASE_MSG + "<green>/lsauth-reset {player}</green>"));
    }

}

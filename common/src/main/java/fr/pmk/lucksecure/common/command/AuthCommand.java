package fr.pmk.lucksecure.common.command;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.logging.Level;

import fr.pmk.lucksecure.common.AuthManager;
import fr.pmk.lucksecure.common.LuckSecure;
import fr.pmk.lucksecure.common.Util;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;

public final class AuthCommand {

    private LuckSecure luckSecure;
    private AuthManager manager;

    public AuthCommand(LuckSecure luckSecure) {
        this.luckSecure = luckSecure;
        this.manager = this.luckSecure.getAuthManager();
    }

    public String permission() {
        return "lsauth.cmd";
    }

    public void execute(Audience sender, String[] args) {
        if (!manager.doesUserHavePermWithAuthContext(sender)) {
            return;
        }

        // CHECK IF THE PLAYER IS ALREADY AUTHENTICATED && IF USER HAS GROUP OR
        // PERMISSION WHICH NEED AUTHENTICATED CONTEXT
        if (!manager.isAuthenticated(sender)) {
            try {
                if (manager.doesUserHaveTotpSetup(sender)) { // CHECK IF 2AF IS ALREADY SETUP
                    if (args.length == 1 && args[0].matches("[0-9]+")) {
                        String code = args[0];
                        try {
                            if (manager.isUserTotpValid(sender, code)) {
                                sender.sendMessage(Util.mm(LuckSecure.LUCKSECURE_BASE_MSG + "<aqua>Authentication succeed !</aqua>"));
                            } else {
                                sender.sendMessage(Util.mm(LuckSecure.LUCKSECURE_BASE_MSG + "<red>Authentication failed...</red>"));
                                help(sender);
                            }
                        } catch (InvalidKeyException | IllegalArgumentException | NoSuchAlgorithmException| IOException e) {
                            sender.sendMessage(Util.mm(LuckSecure.UNHANDLED_EXCEPTION_MSG));
                            this.manager.getLogger().severe("Exception on user TOTP code verification !");
                            this.manager.getLogger().log(Level.SEVERE, e.getMessage(), e);
                        }
                    } else { // INVALID COMMAND FORMAT
                        help(sender);
                    }
                } else {
                    String playerName = sender.get(Identity.NAME).get();

                    String key = manager.generateUserTotpSecret(sender);
                    String label = "LuckSecure" + (this.luckSecure.getConfig().getString("qrcode-label", null) != null ? "@" + this.luckSecure.getConfig().getString("qrcode-label", null) != null : "");
                    String url = AuthManager.generateUserTokenUrl(playerName, key, label);

                    sender.sendMessage(Util.mm(LuckSecure.LUCKSECURE_BASE_MSG + "<aqua>Import this <b><hover:show_text:'KEY:" + key + "'>KEY</hover></b> into your MFA App.</aqua>"));
                    sender.sendMessage(Util.mm(LuckSecure.LUCKSECURE_BASE_MSG + "<aqua>Or scan this <b><hover:show_text:'Click to open.'><click:open_url:'" + url + "'>QRCODE</click></hover></b>.</aqua>"));
                    sender.sendMessage(Util.mm(LuckSecure.LUCKSECURE_BASE_MSG + "<red>Please authenticate yourself to retreive all your access !</red>"));
                }
            } catch (SQLException e) {
                sender.sendMessage(Util.mm(LuckSecure.UNHANDLED_EXCEPTION_MSG));
                this.manager.getLogger().severe("Exception on user TOTP verification !");
                this.manager.getLogger().log(Level.SEVERE, e.getMessage(), e);
            }
        } else {
            sender.sendMessage(Util.mm(LuckSecure.LUCKSECURE_BASE_MSG + "<red>You don't need to authenticate yourself !</red>"));
        }
    }

    private void help(Audience sender) {
        sender.sendMessage(Util.mm(LuckSecure.LUCKSECURE_BASE_MSG + "<red>You need to authenticate yourself to retreive all your access !</red>"));
        sender.sendMessage(Util.mm(LuckSecure.COMMAND_USAGE_BASE_MSG + "<green>/lsauth {code}</green>"));
    }

}

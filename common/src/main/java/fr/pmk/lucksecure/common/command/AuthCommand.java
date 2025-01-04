package fr.pmk.lucksecure.common.command;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.pmk.lucksecure.common.AuthManager;
import net.kyori.adventure.audience.Audience;

public final class AuthCommand {

    // TEXT COMPONENT
    public static final BaseComponent[] LUCKSECURE_BASE_COMPONENTS = new ComponentBuilder().append("[")
            .color(ChatColor.WHITE).bold(true).append("Luck").bold(false).color(ChatColor.AQUA).append("Secure")
            .color(ChatColor.DARK_AQUA).append("]").color(ChatColor.WHITE).bold(true).append(" > ").bold(false)
            .color(ChatColor.DARK_AQUA).create();
    public static final BaseComponent[] COMMAND_USAGE_BASE_COMPONENTS = new ComponentBuilder()
            .append(LUCKSECURE_BASE_COMPONENTS).append("Use : ").color(ChatColor.AQUA).create();
    public static final BaseComponent[] UNHANDLED_EXCEPTION_BASE_COMPONENTS = new ComponentBuilder()
            .append(LUCKSECURE_BASE_COMPONENTS).append("Unhandled exception... contact server admin.")
            .color(ChatColor.RED).create();

    private AuthManager manager;

    public AuthCommand(AuthManager manager) {
        this.manager = manager;
    }

    public boolean execute(Audience sender, String[] args) {
        if (!this.adapter.isSenderInstanceOfPlayer(sender)) {
            this.adapter.sendMessageToSender(sender, new ComponentBuilder("User command only").create());
            return false;
        }

        P player = this.adapter.getPlayerFromSender(sender);
        UUID uuid = this.manager.getPlayerAdapter(player).getUniqueId();

        if (!manager.doesUserHavePermWithAuthContext(player)) {
            return false;
        }

        // CHECK IF THE PLAYER IS ALREADY AUTHENTICATED && IF USER HAS GROUP OR
        // PERMISSION WHICH NEED AUTHENTICATED CONTEXT
        if (!manager.getAuthentificatedUsers().contains(uuid)) {
            try {
                if (manager.doesUserHaveTotpSetup(player)) { // CHECK IF 2AF IS ALREADY SETUP
                    if (args.length == 1 && args[0].matches("[0-9]+")) {
                        String code = args[0];
                        try {
                            if (manager.isUserTotpValid(player, code)) {
                                this.adapter.sendMessageToSender(player,
                                        new ComponentBuilder().append(AuthCommand.LUCKSECURE_BASE_COMPONENTS)
                                                .append("Authentication succeed !").color(ChatColor.AQUA).create());
                                return true;
                            } else {
                                this.adapter.sendMessageToSender(player,
                                        new ComponentBuilder().append(AuthCommand.LUCKSECURE_BASE_COMPONENTS)
                                                .append("Authentication failed...").color(ChatColor.RED).create());
                                help(player);
                            }
                        } catch (InvalidKeyException | IllegalArgumentException | NoSuchAlgorithmException
                                | IOException e) {
                            this.adapter.sendMessageToSender(player,
                                    AuthCommand.UNHANDLED_EXCEPTION_BASE_COMPONENTS);
                            this.logger.severe("Exception on user 2AF code verification !");
                            this.logger.log(Level.SEVERE, e.getMessage(), e);
                        }
                    } else { // INVALID COMMAND FORMAT
                        help(player);
                    }
                } else {
                    String playerName = this.manager.getPlayerAdapter(player).getName();

                    String key = manager.generateUserTotpSecret(player);
                    String url = AuthManager.generateUserTokenUrl(playerName, key,
                            "LuckSecure@" + this.adapter.getServerName());
                    @SuppressWarnings("deprecation")
                    BaseComponent[] urlTextComponent = new ComponentBuilder()
                            .append(AuthCommand.LUCKSECURE_BASE_COMPONENTS)
                            .event(new ClickEvent(Action.OPEN_URL, url))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    new ComponentBuilder().append("Click to open QRCODE").bold(true).create()))
                            .append("Import your key:").color(ChatColor.AQUA)
                            .append(key).bold(true)
                            .append(" or scan the ").bold(false).color(ChatColor.AQUA)
                            .append("QRCODE").bold(true)
                            .append(" with your authenticator app.").bold(false).color(ChatColor.AQUA).create();

                    this.adapter.sendMessageToSender(player, urlTextComponent);
                    this.adapter.sendMessageToSender(player,
                            new ComponentBuilder().append(AuthCommand.LUCKSECURE_BASE_COMPONENTS)
                                    .append("Please authenticate yourself to retreive all your access !")
                                    .color(ChatColor.RED).create());
                    return true;
                }
            } catch (SQLException e) {
                this.adapter.sendMessageToSender(player, AuthCommand.UNHANDLED_EXCEPTION_BASE_COMPONENTS);
                this.logger.severe("Exception on user 2AF setup verification !");
                this.logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } else {
            this.adapter.sendMessageToSender(player,
                    new ComponentBuilder().append(AuthCommand.LUCKSECURE_BASE_COMPONENTS)
                            .append("You don't need to authenticate yourself !").color(ChatColor.RED).create());
        }

        return false;
    }

    private void help(Audience sender) {
        this.adapter.sendMessageToSender(sender,
                new ComponentBuilder().append(AuthCommand.LUCKSECURE_BASE_COMPONENTS)
                        .append("You need to authenticate yourself to retreive all your access !").color(ChatColor.RED)
                        .create());
        this.adapter.sendMessageToSender(sender,
                new ComponentBuilder().append(AuthCommand.COMMAND_USAGE_BASE_COMPONENTS).append("/lsauth {code}")
                        .color(ChatColor.GREEN).create());
    }

}

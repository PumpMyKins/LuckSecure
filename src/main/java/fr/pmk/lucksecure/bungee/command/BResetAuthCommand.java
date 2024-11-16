package fr.pmk.lucksecure.bungee.command;

import fr.pmk.lucksecure.bungee.BungeeAuthManager;
import fr.pmk.lucksecure.bungee.BungeeLuckSecure;
import fr.pmk.lucksecure.command.ResetAuthCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class BResetAuthCommand extends Command {

    private ResetAuthCommand<CommandSender, ProxiedPlayer, BungeeAuthManager, BungeeCommandAdater> command;

    public BResetAuthCommand(BungeeLuckSecure main, BungeeAuthManager manager) {
        super("lsauth-reset", "lsauth.cmd");
        this.command = new ResetAuthCommand<>(BungeeLuckSecure.LOGGER, manager, new BungeeCommandAdater(main));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        this.command.execute(sender, args);
    }
    
}

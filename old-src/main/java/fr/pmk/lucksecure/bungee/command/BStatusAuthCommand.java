package fr.pmk.lucksecure.bungee.command;

import fr.pmk.lucksecure.bungee.BungeeAuthManager;
import fr.pmk.lucksecure.bungee.BungeeLuckSecure;
import fr.pmk.lucksecure.command.StatusAuthCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class BStatusAuthCommand extends Command {

    private StatusAuthCommand<CommandSender, ProxiedPlayer, BungeeAuthManager, BungeeCommandAdater> command;

    public BStatusAuthCommand(BungeeLuckSecure main, BungeeAuthManager manager) {
        super("lsauth-status", "lsauth.cmd");
        this.command = new StatusAuthCommand<>(BungeeLuckSecure.LOGGER, manager, new BungeeCommandAdater(main));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        this.command.execute(sender, args);
    }
    
}

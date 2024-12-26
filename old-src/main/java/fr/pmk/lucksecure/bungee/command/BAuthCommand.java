package fr.pmk.lucksecure.bungee.command;

import fr.pmk.lucksecure.bungee.BungeeAuthManager;
import fr.pmk.lucksecure.bungee.BungeeLuckSecure;
import fr.pmk.lucksecure.command.AuthCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class BAuthCommand extends Command {

    private AuthCommand<CommandSender, ProxiedPlayer, BungeeAuthManager, BungeeCommandAdater> command;

    public BAuthCommand(BungeeLuckSecure main, BungeeAuthManager manager) {
        super("lsauth", "");
        this.command = new AuthCommand<>(BungeeLuckSecure.LOGGER, manager, new BungeeCommandAdater(main));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        this.command.execute(sender, args);
    }
    
}

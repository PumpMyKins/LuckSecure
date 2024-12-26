package fr.pmk.lucksecure.bukkit.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.pmk.lucksecure.bukkit.BukkitAuthManager;
import fr.pmk.lucksecure.bukkit.BukkitLuckSecure;
import fr.pmk.lucksecure.command.AuthCommand;

public class BAuthCommand implements CommandExecutor {

    private AuthCommand<CommandSender, Player, BukkitAuthManager, BukkitCommandAdater> command;

    public BAuthCommand(BukkitLuckSecure main, BukkitAuthManager manager) {
        this.command = new AuthCommand<>(BukkitLuckSecure.LOGGER, manager, new BukkitCommandAdater(main));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label,
            String[] args) {
        return this.command.execute(sender, args);
    }
    
}

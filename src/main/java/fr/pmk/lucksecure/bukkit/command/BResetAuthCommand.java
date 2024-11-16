package fr.pmk.lucksecure.bukkit.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.pmk.lucksecure.bukkit.BukkitAuthManager;
import fr.pmk.lucksecure.bukkit.BukkitLuckSecure;
import fr.pmk.lucksecure.command.ResetAuthCommand;

public class BResetAuthCommand implements CommandExecutor {

    private ResetAuthCommand<CommandSender, Player, BukkitAuthManager, BukkitCommandAdater> command;

    public BResetAuthCommand(BukkitLuckSecure main, BukkitAuthManager manager) {
        this.command = new ResetAuthCommand<>(BukkitLuckSecure.LOGGER, manager, new BukkitCommandAdater(main));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label,
            String[] args) {
        return this.command.execute(sender, args);
    }
    
}

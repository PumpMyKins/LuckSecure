package fr.pmk.lucksecure.paper.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.pmk.lucksecure.common.LuckSecure;
import fr.pmk.lucksecure.common.command.AuthCommand;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;

public class PAuthCommand implements BasicCommand {
    
    private AuthCommand command;

    public PAuthCommand(LuckSecure luckSecure) {
        this.command = new AuthCommand(luckSecure);
    }

    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if (!(sender instanceof Player)) {
            sender.sendMessage("Player command only");
            return;
        }
        command.execute(sender, args);
    }

}

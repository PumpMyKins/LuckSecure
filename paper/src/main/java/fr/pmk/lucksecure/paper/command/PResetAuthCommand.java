package fr.pmk.lucksecure.paper.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.pmk.lucksecure.common.LuckSecure;
import fr.pmk.lucksecure.common.command.ResetAuthCommand;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;

public class PResetAuthCommand implements BasicCommand {
    
    private ResetAuthCommand command;

    public PResetAuthCommand(LuckSecure luckSecure) {
        this.command = new ResetAuthCommand(luckSecure);
    }

    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if (sender instanceof Player) {
            sender.sendMessage("Console command only");
            return;
        }
        command.execute(sender, args);
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return !(sender instanceof Player);
    }

}

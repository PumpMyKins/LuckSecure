package fr.pmk.lucksecure.paper.command;

import fr.pmk.lucksecure.common.AuthManager;
import fr.pmk.lucksecure.common.command.StatusAuthCommand;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;

public class PStatusAuthCommand implements BasicCommand {
    
    private StatusAuthCommand command;

    public PStatusAuthCommand(AuthManager manager) {
        this.command = new StatusAuthCommand(manager);
    }

    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        command.execute(stack.getSender(), args);
    }

}

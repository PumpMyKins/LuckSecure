package fr.pmk.lucksecure.velocity.command;

import com.velocitypowered.api.command.SimpleCommand;

import fr.pmk.lucksecure.common.AuthManager;
import fr.pmk.lucksecure.common.command.StatusAuthCommand;

public class VStatusAuthCommand implements SimpleCommand {
    
    private StatusAuthCommand command;

    public VStatusAuthCommand(AuthManager manager) {
        this.command = new StatusAuthCommand(manager);
    }

    @Override
    public void execute(Invocation invocation) {
        command.execute(invocation.source(), invocation.arguments());
    }

}

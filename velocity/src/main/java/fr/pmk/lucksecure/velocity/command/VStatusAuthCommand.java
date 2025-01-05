package fr.pmk.lucksecure.velocity.command;

import com.velocitypowered.api.command.SimpleCommand;

import fr.pmk.lucksecure.common.LuckSecure;
import fr.pmk.lucksecure.common.command.StatusAuthCommand;

public class VStatusAuthCommand implements SimpleCommand {
    
    private StatusAuthCommand command;

    public VStatusAuthCommand(LuckSecure luckSecure) {
        this.command = new StatusAuthCommand(luckSecure);
    }

    @Override
    public void execute(Invocation invocation) {
        command.execute(invocation.source(), invocation.arguments());
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission(command.permission());
    }

}

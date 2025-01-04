package fr.pmk.lucksecure.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;

import fr.pmk.lucksecure.common.AuthManager;
import fr.pmk.lucksecure.common.command.AuthCommand;

public class VAuthCommand implements SimpleCommand {
    
    private AuthCommand command;

    public VAuthCommand(AuthManager manager) {
        this.command = new AuthCommand(manager);
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        if (!(source instanceof Player)) {
            source.sendPlainMessage("Player command only");
            return;
        }
        command.execute(source, invocation.arguments());
    }

}

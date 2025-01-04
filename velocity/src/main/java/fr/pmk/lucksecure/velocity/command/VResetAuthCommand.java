package fr.pmk.lucksecure.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;

import fr.pmk.lucksecure.common.LuckSecure;
import fr.pmk.lucksecure.common.command.ResetAuthCommand;

public class VResetAuthCommand implements SimpleCommand {
    
    private ResetAuthCommand command;

    public VResetAuthCommand(LuckSecure luckSecure) {
        this.command = new ResetAuthCommand(luckSecure);
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        if (source instanceof Player) {
            source.sendPlainMessage("Console command only");
            return;
        }
        command.execute(source, invocation.arguments());
    }

}

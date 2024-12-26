package fr.pmk.lucksecure.bungee.command;

import fr.pmk.lucksecure.bungee.BungeeLuckSecure;
import fr.pmk.lucksecure.command.ICommandAdapter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeCommandAdater implements ICommandAdapter<CommandSender, ProxiedPlayer> {

    private BungeeLuckSecure main;

    public BungeeCommandAdater(BungeeLuckSecure main) {
        this.main = main;
    }

    @Override
    public boolean isSenderInstanceOfPlayer(CommandSender sender) {
        return sender instanceof ProxiedPlayer;
    }

    @Override
    public void sendMessageToSender(CommandSender sender, BaseComponent[] msg) {
        sender.sendMessage(msg);
    }

    @Override
    public ProxiedPlayer getPlayerFromSender(CommandSender sender) {
        return (ProxiedPlayer) sender;
    }

    @Override
    public ProxiedPlayer getServerPlayer(String name) {
        return this.main.getProxy().getPlayer(name);
    }

    @Override
    public String getServerName() {
        return this.main.getProxy().getName();
    }
    
}

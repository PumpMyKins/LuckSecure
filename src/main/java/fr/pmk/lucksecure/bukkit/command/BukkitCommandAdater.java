package fr.pmk.lucksecure.bukkit.command;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.pmk.lucksecure.bukkit.BukkitLuckSecure;
import fr.pmk.lucksecure.command.ICommandAdapter;
import net.md_5.bungee.api.chat.BaseComponent;

public class BukkitCommandAdater implements ICommandAdapter<CommandSender, Player> {

    private BukkitLuckSecure main;

    public BukkitCommandAdater(BukkitLuckSecure main) {
        this.main = main;
    }

    @Override
    public boolean isSenderInstanceOfPlayer(CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public void sendMessageToSender(CommandSender sender, BaseComponent[] msg) {
        sender.sendMessage(Arrays.toString(msg));
    }

    @Override
    public Player getPlayerFromSender(CommandSender sender) {
        return (Player) sender;
    }

    @Override
    public Player getServerPlayer(String name) {
        return this.main.getServer().getPlayer(name);
    }

    @Override
    public String getServerName() {
        return this.main.getServer().getName();
    }
    
}

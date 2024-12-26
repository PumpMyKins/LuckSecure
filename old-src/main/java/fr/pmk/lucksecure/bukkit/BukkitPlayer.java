package fr.pmk.lucksecure.bukkit;

import java.util.UUID;

import org.bukkit.entity.Player;

import fr.pmk.lucksecure.IAuthUserAdapter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;

public class BukkitPlayer implements IAuthUserAdapter {

    private LuckPerms api;
    private Player player;

    public BukkitPlayer(LuckPerms api, Player player) {
        this.api = api;
        this.player = player;
    }

    @Override
    public String getName() {
        return this.player.getName();
    }

    @Override
    public UUID getUniqueId() {
        return this.player.getUniqueId();
    }

    @Override
    public User getLuckPermsUser() {
        return this.api.getPlayerAdapter(Player.class).getUser(this.player);
    }

}

package fr.pmk.lucksecure.bungee;

import java.util.UUID;

import fr.pmk.lucksecure.IAuthUserAdapter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeePlayer implements IAuthUserAdapter {

    private LuckPerms api;
    private ProxiedPlayer player;

    public BungeePlayer(LuckPerms api, ProxiedPlayer player) {
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
        return this.api.getPlayerAdapter(ProxiedPlayer.class).getUser(this.player);
    }
    
}

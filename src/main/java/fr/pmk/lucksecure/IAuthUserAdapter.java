package fr.pmk.lucksecure;

import java.util.UUID;

import net.luckperms.api.model.user.User;

public interface IAuthUserAdapter {
    public String getName();
    public UUID getUniqueId();
    public User getLuckPermsUser();
}

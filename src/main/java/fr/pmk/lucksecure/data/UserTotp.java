package fr.pmk.lucksecure.data;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "tokens")
public class UserTotp {

    public UserTotp() {}
    
    public UserTotp(UUID id, String token) {
        this.id = id;
        this.token = token;
    }

    @DatabaseField(id = true, canBeNull = false, unique = true)
    private UUID id;

    @DatabaseField(canBeNull = false)
    private String token;

    public UUID getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

}

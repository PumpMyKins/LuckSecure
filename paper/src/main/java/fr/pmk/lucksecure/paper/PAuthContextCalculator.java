package fr.pmk.lucksecure.paper;

import org.bukkit.entity.Player;

import fr.pmk.lucksecure.common.AuthContextCalculator;
import fr.pmk.lucksecure.common.AuthManager;
import net.kyori.adventure.audience.Audience;

public class PAuthContextCalculator extends AuthContextCalculator<Player> {

    public PAuthContextCalculator(AuthManager authManager) {
        super(authManager);
    }

    @Override
    protected Audience getAudienceFromTarget(Player target) {
        return target;
    }
    
}

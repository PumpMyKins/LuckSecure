package fr.pmk.lucksecure.velocity;

import com.velocitypowered.api.proxy.Player;

import fr.pmk.lucksecure.common.AuthContextCalculator;
import fr.pmk.lucksecure.common.AuthManager;
import net.kyori.adventure.audience.Audience;

public class VAuthContextCalculator extends AuthContextCalculator<Player> {

    public VAuthContextCalculator(AuthManager authManager) {
        super(authManager);
    }

    @Override
    protected Audience getAudienceFromTarget(Player target) {
        return target;
    }
    
}

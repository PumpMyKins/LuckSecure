package fr.pmk;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class AuthContextCalculator implements ContextCalculator<ProxiedPlayer> {

    private static final String KEY = "lucksecure";
    private static final String AUTH = "authenticated";
    private static final String NOT_AUTH = "not-authenticated";

    private AuthManager authManager;

    public AuthContextCalculator(AuthManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public void calculate(@NonNull ProxiedPlayer target, @NonNull ContextConsumer consumer) {
        if (authManager.getAuthentificatedUsers().contains(target.getUniqueId().toString())) {
            consumer.accept(KEY, AUTH);
        } else {
            consumer.accept(KEY, NOT_AUTH);
        }       
    }

    @Override
    public ContextSet estimatePotentialContexts() {
        ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
        builder.add(KEY, AUTH);
        builder.add(KEY, NOT_AUTH);
        return builder.build();
    }
    
}

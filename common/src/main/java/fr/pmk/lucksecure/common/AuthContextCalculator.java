package fr.pmk.lucksecure.common;

import net.kyori.adventure.audience.Audience;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;

public abstract class AuthContextCalculator<T> implements ContextCalculator<T> {

    public static final String KEY = "lucksecure";
    public static final String AUTH = "authenticated";
    private static final String NOT_AUTH = "not-authenticated";

    private AuthManager authManager;

    public AuthContextCalculator(AuthManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public void calculate(T target, ContextConsumer consumer) {
        if (authManager.isAuthenticated(getAudienceFromTarget(target))) {
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

    protected abstract Audience getAudienceFromTarget(T target);
    
}

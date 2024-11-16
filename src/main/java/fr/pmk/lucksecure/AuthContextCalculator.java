package fr.pmk.lucksecure;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;

public class AuthContextCalculator<T> implements ContextCalculator<T> {

    public static final String KEY = "lucksecure";
    public static final String AUTH = "authenticated";
    private static final String NOT_AUTH = "not-authenticated";

    private AuthManager<T, ?> authManager;

    public AuthContextCalculator(AuthManager<T, ?> authManager) {
        this.authManager = authManager;
    }

    @Override
    public void calculate(@NonNull T target, @NonNull ContextConsumer consumer) {
        if (authManager.getAuthentificatedUsers().contains(this.authManager.getPlayerAdapter(target).getUniqueId())) {
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

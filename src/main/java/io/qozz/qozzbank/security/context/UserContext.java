package io.qozz.qozzbank.security.context;

import io.qozz.qozzbank.domain.entity.UserEntity;

import java.util.UUID;

public class UserContext {
    private static final ThreadLocal<UserEntity> userHolder = new ThreadLocal<>();
    private static final ThreadLocal<UUID> authIdHolder = new ThreadLocal<>();

    public static void setContext(UserEntity user, UUID authId) {
        userHolder.set(user);
        authIdHolder.set(authId);
    }

    public static UserEntity getUser() {
        return userHolder.get();
    }

    public static UUID getAuthId() {
        return authIdHolder.get();
    }

    public static void clear() {
        userHolder.remove();
        authIdHolder.remove();
    }
}

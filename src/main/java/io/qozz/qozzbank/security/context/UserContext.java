package io.qozz.qozzbank.security.context;

import java.util.UUID;

public class UserContext {
    private static final ThreadLocal<String> tokenHolder = new ThreadLocal<>();
    private static final ThreadLocal<UUID> userIdHolder = new ThreadLocal<>();

    public static void setContext(String token, UUID userId) {
        tokenHolder.set(token);
        userIdHolder.set(userId);
    }

    public static String getToken() {
        return tokenHolder.get();
    }

    public static UUID getUserId() {
        return userIdHolder.get();
    }

    public static void clear() {
        tokenHolder.remove();
        userIdHolder.remove();
    }
}

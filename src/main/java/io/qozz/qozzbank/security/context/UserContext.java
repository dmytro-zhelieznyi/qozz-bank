package io.qozz.qozzbank.security.context;

import io.qozz.qozzbank.domain.entity.UserEntity;

public class UserContext {
    private static final ThreadLocal<UserEntity> userHolder = new ThreadLocal<>();

    public static void setContext(UserEntity user) {
        userHolder.set(user);
    }

    public static UserEntity getUser() {
        return userHolder.get();
    }

    public static void clear() {
        userHolder.remove();
    }
}

package ru.belov.ourabroad.core.domain;

import ru.belov.ourabroad.core.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.Objects;

public final class UserFactory {

    private UserFactory() {
    }

    public static User fromDb(
            String id,
            String email,
            String phone,
            String passwordHash,
            UserStatus status,
            String telegramUsername,
            String whatAppNumber,
            String activity,
            LocalDateTime createdAt,
            LocalDateTime lastLoginAt
    ) {
        return User.create(
                id,
                email,
                phone,
                passwordHash,
                status,
                telegramUsername,
                whatAppNumber,
                activity,
                createdAt,
                lastLoginAt
        );
    }

    public static User newUser(
            String id,
            String email,
            String phone,
            String passwordHash,
            String telegramUsername,
            String whatAppNumber,
            String activity
    ) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(email);
        Objects.requireNonNull(passwordHash);

        return User.create(
                id,
                email,
                phone,
                passwordHash,
                UserStatus.ACTIVE,
                telegramUsername,
                whatAppNumber,
                activity,
                LocalDateTime.now(),
                null
        );
    }
}

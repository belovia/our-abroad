package ru.belov.ourabroad.core.domain;

import ru.belov.ourabroad.core.domain.User;
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
            LocalDateTime createdAt,
            LocalDateTime lastLoginAt
    ) {
        return User.restore(
                id,
                email,
                phone,
                passwordHash,
                status,
                createdAt,
                lastLoginAt
        );
    }

    public static User newUser(
            String id,
            String email,
            String phone,
            String passwordHash
    ) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(email);
        Objects.requireNonNull(passwordHash);

        return User.restore(
                id,
                email,
                phone,
                passwordHash,
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                null
        );
    }
}

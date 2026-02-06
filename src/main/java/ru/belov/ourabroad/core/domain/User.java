package ru.belov.ourabroad.core.domain;

import lombok.Getter;
import lombok.Setter;
import ru.belov.ourabroad.core.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.Objects;
@Setter
@Getter
public class User {

    private final String id;
    private String email;
    private String phone;
    private String password;
    private UserStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    private User(String id, LocalDateTime createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public static User restore(
            String id,
            String email,
            String phone,
            String password,
            UserStatus status,
            LocalDateTime createdAt,
            LocalDateTime lastLoginAt
    ) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(createdAt);

        User user = new User(id, createdAt);
        user.email = email;
        user.phone = phone;
        user.password = password;
        user.status = status;
        user.lastLoginAt = lastLoginAt;
        return user;
    }


    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void block() {
        this.status = UserStatus.BLOCKED;
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
    }
}

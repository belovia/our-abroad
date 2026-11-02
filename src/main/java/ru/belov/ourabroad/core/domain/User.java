package ru.belov.ourabroad.core.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.belov.ourabroad.core.enums.UserStatus;
import ru.belov.ourabroad.core.security.AppRoles;

import java.time.LocalDateTime;
import java.util.Objects;
@Setter
@Getter
@ToString
public class User {

    private final String id;
    private String email;
    private String phone;
    private String password;
    private UserStatus status;

    private String telegramUsername;
    private String whatsappNumber;
    private String activity;
    private String roles;
    private final LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    private User(String id, LocalDateTime createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public static User create(
            String id,
            String email,
            String phone,
            String password,
            UserStatus status,
            String telegramUsername,
            String whatsappNumber,
            String activity,
            String roles,
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
        user.telegramUsername = telegramUsername;
        user.whatsappNumber = whatsappNumber;
        user.activity = activity;
        user.roles = roles != null && !roles.isBlank() ? roles : AppRoles.DEFAULT;
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

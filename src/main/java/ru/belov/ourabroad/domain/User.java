package ru.belov.ourabroad.domain;

import lombok.Getter;
import ru.belov.ourabroad.enums.UserStatus;

import java.time.LocalDateTime;

@Getter
public class User {
    private final String id;
    private final String email;
    private final String phone;
    private final String password;
    private UserStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;


    public User(
            String id,
            String email,
            String phone,
            String password,
            UserStatus status,
            LocalDateTime createdAt,
            LocalDateTime lastLoginAt
    ) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.status = status;
        this.createdAt = createdAt;
        this.lastLoginAt = lastLoginAt;
    }


    public void block() {
        this.status = UserStatus.BLOCKED;
    }


    public void updateLastLogin(LocalDateTime time) {
        this.lastLoginAt = time;
    }
}

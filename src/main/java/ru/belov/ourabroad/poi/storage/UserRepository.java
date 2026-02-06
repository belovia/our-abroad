package ru.belov.ourabroad.poi.storage;

import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.core.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(String id);

    Optional<User> findByEmail(String email, String userId);

    void save(User user);

    boolean updateLastLogin(String userId, LocalDateTime lastLoginAt);

    boolean updateStatus(String userId, UserStatus status);
}

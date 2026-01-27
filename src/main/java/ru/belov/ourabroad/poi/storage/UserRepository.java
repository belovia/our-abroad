package ru.belov.ourabroad.poi.storage;

import ru.belov.ourabroad.domain.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    void save(User user);
    void update(User user);
    void deleteUser(String userId);
}

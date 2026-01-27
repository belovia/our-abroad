package ru.belov.ourabroad.poi.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.belov.ourabroad.domain.User;
import ru.belov.ourabroad.poi.storage.UserRepository;

import java.util.Optional;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    @Override
    public Optional<User> findById(String id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public void save(User user) {
        log.info("Saving user");
    }

    @Override
    public void update(User user) {
        log.info("Updating user");
    }

    @Override
    public void deleteUser(String userId) {
        log.info("Deleting user");
    }
}

package ru.belov.ourabroad.api.usecases.services.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.poi.storage.UserRepository;

import java.util.Optional;

import static ru.belov.ourabroad.web.validators.ErrorCode.USER_NOT_FOUND;
import static ru.belov.ourabroad.web.validators.ErrorCode.REQUEST_VALIDATION_ERROR;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findById(String userId, Context context) {
        log.info("[userId: {}] Try to find user by id", userId);

        Optional<User> fromDbOpt = userRepository.findById(userId);
        if (fromDbOpt.isEmpty()) {
            log.warn("[userId: {}] User not found", userId);
            context.setError(USER_NOT_FOUND);
            return null;
        }

        User user = fromDbOpt.get();
        log.info("[userId: {}] Found: {}", userId, user);
        return user;
    }

    @Override
    public User findByEmail(String email, Context context) {
        log.info("Try to find user by email: {}",email);

        Optional<User> fromDbOpt = userRepository.findByEmail(email);
        if (fromDbOpt.isEmpty()) {
            log.warn("User not found by email: {}", email);
            context.setError(USER_NOT_FOUND);
            return null;
        }

        User user = fromDbOpt.get();
        log.info("[userId: {}] Found: {}", user.getId(), user);
        return user;
    }

    @Override
    public User findByPhone(String userId, String phone, Context context) {
        log.info("Try to find user by phone");

        Optional<User> fromDbOpt = userRepository.findById(userId);
        if (fromDbOpt.isEmpty()) {
            log.warn("[userId: {}] User not found", userId);
            context.setError(USER_NOT_FOUND);
            return null;
        }

        User user = fromDbOpt.get();
        log.info("[userId: {}] Found: {}", userId, user);
        return user;
    }

    @Override
    public void save(User user, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        if (user == null) {
            context.setError(REQUEST_VALIDATION_ERROR);
            return;
        }
        log.info("[userId: {}] Saving new user", user.getId());
        userRepository.save(user);
        log.info("[userId: {}] User saved successfully", user.getId());
    }

    @Override
    public void update(User user, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        if (user == null) {
            context.setError(REQUEST_VALIDATION_ERROR);
            return;
        }
        log.info("[userId: {}] Updating user", user.getId());
        userRepository.save(user);
        log.info("[userId: {}] User updated successfully", user.getId());
    }

    @Override
    public boolean existsByEmail(String userId, String email, Context context) {
        log.info("[userId: {}] Checking if email already exists: {}", userId, email);
        boolean exists = userRepository.existsByEmail(userId, email);
        log.info("[userId: {}] Email exists: {}", userId, exists);
        return exists;
    }
}

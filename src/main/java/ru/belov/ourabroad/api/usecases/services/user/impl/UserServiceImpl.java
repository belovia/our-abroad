package ru.belov.ourabroad.api.usecases.services.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.poi.storage.UserRepository;
import ru.belov.ourabroad.web.validators.ErrorCode;

import java.util.Optional;

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
            context.setError(ErrorCode.USER_NOT_FOUND);
            return null;
        }

        User user = fromDbOpt.get();
        log.info("[userId: {}] Found: {}", userId, user);
        return user;
    }

    @Override
    public User findByEmail(String userId, String userEmail, Context context) {
        log.info("[userId: {}] Try to find user by email", userId);
        Optional<User> fromDbOpt = userRepository.findById(userId);
        if (fromDbOpt.isEmpty()) {
            log.warn("[userId: {}] User not found", userId);
            context.setError(ErrorCode.USER_NOT_FOUND);
            return null;
        }

        User user = fromDbOpt.get();
        log.info("[userId: {}] Found: {}", userId, user);
        return user;
    }

    @Override
    public User findByPhone(String userId, String userEmail, Context context) {
        log.info("[userId: {}] Try to find user by phone", userId);
        Optional<User> fromDbOpt = userRepository.findById(userId);
        if (fromDbOpt.isEmpty()) {
            log.warn("[userId: {}] User not found", userId);
            context.setError(ErrorCode.USER_NOT_FOUND);
            return null;
        }

        User user = fromDbOpt.get();
        log.info("[userId: {}] Found: {}", userId, user);
        return user;
    }

    @Override
    public boolean update(User user, Context context) {
        return false;
    }
}

package ru.belov.ourabroad.api.usecases.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.belov.ourabroad.api.usecases.UserUpdateUsecase;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.poi.storage.UserRepository;
import ru.belov.ourabroad.poi.storage.exceptions.UserAlreadyExistsException;
import ru.belov.ourabroad.poi.storage.exceptions.UserNotFoundException;
import ru.belov.ourabroad.poi.storage.exceptions.ValidationException;
import ru.belov.ourabroad.web.validators.UserValidator;
import ru.belov.ourabroad.web.validators.ValidationResult;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserUpdateUsecaseImpl implements UserUpdateUsecase {

    private final UserRepository userRepository;
    private final UserValidator userValidator;

    @Override
    public void updateEmail(String userId, String email) {

        if (!StringUtils.hasText(userId)) {
            log.warn("UpdateUserEmail called with empty userId");
            throw new IllegalArgumentException("userId is empty");
        }

        ValidationResult result = userValidator.validateEmail(email);

        if (!result.isValid()) {
            throw new ValidationException(result);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.info("[userId: {}] User not found", userId);
                    return new UserNotFoundException(userId);
                });

        boolean emailExists = userRepository.existsByEmail(email);

        if (emailExists) {
            throw new UserAlreadyExistsException("email");
        }

        user.setEmail(email);
        userRepository.save(user);
    }

    @Override
    public void updatePassword(String userId, String password) {

        if (!StringUtils.hasText(userId)) {
            log.warn("UpdateUserEmail called with empty userId");
            throw new IllegalArgumentException("userId is empty");
        }

        ValidationResult result = userValidator.validatePassword(password);

        if (!result.isValid()) {
            throw new ValidationException(result);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.info("[userId: {}] User not found", userId);
                    return new UserNotFoundException(userId);
                });

        user.setPassword(password);

        userRepository.save(user);
    }

    @Override
    public void updatePhone(String userId, String phone) {

        if (!StringUtils.hasText(userId)) {
            log.warn("UpdateUserEmail called with empty userId");
            throw new IllegalArgumentException("userId is empty");
        }

        ValidationResult result = userValidator.validatePhone(phone);

        if (!result.isValid()) {
            throw new ValidationException(result);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.info("[userId: {}] User not found", userId);
                    return new UserNotFoundException(userId);
                });

        user.setPhone(phone);

        userRepository.save(user);
    }
}

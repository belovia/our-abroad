package ru.belov.ourabroad.api.usecases.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.GetUserUsecase;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.poi.storage.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserUsecaseImpl implements GetUserUsecase {

    private final UserRepository userRepository;

    @Override
    public User getUserById(String userId) {
        if (!StringUtils.isNotBlank(userId)) {
            log.warn("GetUserById called with empty userId");
            return null;
        }

        return userRepository.findById(userId)
                .orElseGet(() -> {
                    log.info("User not found by id={}", userId);
                    return null;
                });
    }

    @Override
    public User getUserByEmail(String email) {
        if (!StringUtils.isNotBlank(email)) {
            log.warn("GetUserByEmail called with empty email");
            return null;
        }

        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    log.info("User not found by email={}", email);
                    return null;
                });
    }
}

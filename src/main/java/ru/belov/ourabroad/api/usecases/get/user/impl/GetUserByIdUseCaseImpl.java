package ru.belov.ourabroad.api.usecases.get.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.get.user.GetUserByIdUsecase;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.poi.storage.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserByIdUseCaseImpl implements GetUserByIdUsecase {

    private final UserService userService;

    @Override
    public User getUserById(String userId) {

        if (!StringUtils.isNotBlank(userId)) {
            log.warn("GetUserById called with empty userId");
            return null;
        }

        log.info("[userId: {}] Start get user by id", userId);

        return userRepository.findById(userId)
                .orElseGet(() -> {
                    log.info("[userId: {}] User not found", userId);
                    return null;
                });
    }
}

package ru.belov.ourabroad.api.usecases.get.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.get.GetUserByIdUsecase;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.poi.storage.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserByIdUseCaseImpl implements GetUserByIdUsecase {

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
}

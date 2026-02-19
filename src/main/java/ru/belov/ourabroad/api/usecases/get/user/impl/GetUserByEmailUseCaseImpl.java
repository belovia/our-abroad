package ru.belov.ourabroad.api.usecases.get.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.get.user.GetUserByEmailUseCase;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.poi.storage.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserByEmailUseCaseImpl implements GetUserByEmailUseCase {

    private final UserRepository userRepository;

    @Override
    public User getUserByEmail(String email) {

        if (!StringUtils.isNotBlank(email)) {
            log.warn("GetUserByEmail called with empty email");
            return null;
        }

        log.info("[email: {}] Start get user by email", email);

        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    log.info("[email: {}] User not found", email);
                    return null;
                });
    }

}

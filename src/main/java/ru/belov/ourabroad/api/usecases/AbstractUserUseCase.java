package ru.belov.ourabroad.api.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.poi.storage.UserRepository;
import ru.belov.ourabroad.poi.storage.exceptions.UserNotFoundException;

@RequiredArgsConstructor
@Slf4j
public class AbstractUserUseCase {
    protected final UserRepository userRepository;

    protected User getUserOrThrow(String userId) {
        if (!StringUtils.hasText(userId)) {
            log.warn("UserId is blank");
            throw new IllegalArgumentException("userId is required");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.info("[userId: {}] User not found", userId);
                    return new UserNotFoundException(userId);
                });
    }
}

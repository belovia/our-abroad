package ru.belov.ourabroad.api.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.poi.storage.UserRepository;

import java.util.Optional;

import static ru.belov.ourabroad.web.validators.ErrorCode.USER_NOT_FOUND;

@RequiredArgsConstructor
@Slf4j
public class AbstractUserUseCase {
    
    protected final UserRepository userRepository;

    protected User findExistsUser(String userId, Context context) {

        if (!context.isSuccess()){
            return null;
        }

        log.info("[userId: {}] Try to find user with id: {}", userId, userId);
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            context.setError(USER_NOT_FOUND);
            log.info("[userId: {}] User not found", userId);
            return null;
        }

        User user = userOpt.get();
        log.info("[userId: {}] Found: {}", userId, user);
        return user;
    }
}

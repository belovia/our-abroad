package ru.belov.ourabroad.api.usecases.get.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.get.user.GetUserByEmailUseCase;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserByEmailUseCaseImpl implements GetUserByEmailUseCase {

    private final UserService userService;

    @Override
    public User getUserByEmail(String userId, String email) {

        Context context = new Context();
        if (!StringUtils.isNotBlank(email)) {
            log.warn("[userId: {}] GetUserByEmail called with empty email", userId);
            return null;
        }

        log.info("[userId: {}] Start get user by email", userId);

        return userService.findByEmail(userId, email, context);
    }

}

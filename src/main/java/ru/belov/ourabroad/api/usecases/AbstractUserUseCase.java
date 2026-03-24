package ru.belov.ourabroad.api.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;

@RequiredArgsConstructor
@Slf4j
public class AbstractUserUseCase {

    protected final UserService userService;

    protected User findExistsUser(String userId, Context context) {
        if (!context.isSuccess()) {
            return null;
        }
        return userService.findById(userId, context);
    }
}

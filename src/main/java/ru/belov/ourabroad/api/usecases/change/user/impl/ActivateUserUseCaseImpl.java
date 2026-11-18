package ru.belov.ourabroad.api.usecases.change.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.AbstractUserUseCase;
import ru.belov.ourabroad.api.usecases.change.user.ActivateUserUseCase;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.UserValidator;

@Service
@Slf4j
@Transactional
public class ActivateUserUseCaseImpl extends AbstractUserUseCase implements ActivateUserUseCase {

    private final UserValidator userValidator;

    public ActivateUserUseCaseImpl(UserService userService, UserValidator userValidator) {
        super(userService);
        this.userValidator = userValidator;
    }

    @Override
    public Response execute(Request request) {
        Context context = new Context();
        String userId = request.userId();
        log.info("[userId: {}] Activate user", userId);

        userValidator.validateId(userId, context);
        if (!context.isSuccess()) {
            return errorResponse(userId, context);
        }

        User user = findExistsUser(userId, context);
        if (!context.isSuccess() || user == null) {
            return errorResponse(userId, context);
        }

        user.activate();
        userService.update(user, context);
        if (!context.isSuccess()) {
            return errorResponse(userId, context);
        }

        log.info("[userId: {}] User activated", userId);
        return new Response(userId, true, ErrorCode.SUCCESS.getMessage());
    }

    private Response errorResponse(String userId, Context context) {
        log.error("[userId: {}] Activate failed: {}", userId, context.getErrorMessage());
        return new Response(userId, false, context.getErrorMessage());
    }
}

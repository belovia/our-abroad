package ru.belov.ourabroad.api.usecases.change.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.AbstractUserUseCase;
import ru.belov.ourabroad.api.usecases.change.user.ChangeUserPhoneUseCase;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.config.security.CurrentUserProvider;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.UserValidator;

@Service
@Slf4j
@Transactional
public class ChangeUserPhoneUseCaseImpl extends AbstractUserUseCase implements ChangeUserPhoneUseCase {

    private final UserValidator userValidator;
    private final CurrentUserProvider currentUserProvider;

    public ChangeUserPhoneUseCaseImpl(
            UserService userService,
            UserValidator userValidator,
            CurrentUserProvider currentUserProvider
    ) {
        super(userService);
        this.userValidator = userValidator;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public Response execute(Request request) {
        Context context = new Context();
        String userId = currentUserProvider.requiredUserId();
        log.info("[userId: {}] Start changing phone", userId);

        validateRequest(request, context);
        if (!context.isSuccess()) {
            log.info("[userId: {}] Validation failed", userId);
            return errorResponse(context, userId);
        }

        User user = findExistsUser(userId, context);
        if (!context.isSuccess() || user == null) {
            log.info("[userId: {}] Failed to retrieve user", userId);
            return errorResponse(context, userId);
        }

        user.setPhone(request.newPhone());
        userService.update(user, context);

        log.info("[userId: {}] Phone changed successfully", userId);
        return successResponse(userId);
    }

    private void validateRequest(Request request, Context context) {
        log.info("Validating request");
        userValidator.validatePhone(request.newPhone(), context);
        if (context.isSuccess()) {
            log.info("Validation success");
        } else {
            log.error("Validation failed");
        }
    }

    private Response errorResponse(Context context, String userId) {
        log.error("[userId: {}] Returning error response", userId);
        return new Response(userId, false, context.getErrorMessage());
    }

    private Response successResponse(String userId) {
        log.info("[userId: {}] Returning success response", userId);
        return new Response(userId, true, ErrorCode.SUCCESS.getMessage());
    }
}

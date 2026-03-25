package ru.belov.ourabroad.api.usecases.change.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.AbstractUserUseCase;
import ru.belov.ourabroad.api.usecases.change.user.ChangeUserPasswordUseCase;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.UserValidator;

@Service
@Slf4j
@Transactional
public class ChangeUserPasswordUseCaseImpl extends AbstractUserUseCase implements ChangeUserPasswordUseCase {

    private final UserValidator userValidator;

    public ChangeUserPasswordUseCaseImpl(
            UserService userService,
            UserValidator userValidator
    ) {
        super(userService);
        this.userValidator = userValidator;
    }

    @Override
    public Response execute(Request request) {
        Context context = new Context();
        String userId = request.userId();
        log.info("[userId: {}] Start changing password", userId);

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

        verifyOldPassword(request.oldPassword(), user.getPassword(), context);
        if (!context.isSuccess()) {
            log.info("[userId: {}] Old password mismatch", userId);
            return errorResponse(context, userId);
        }

        user.setPassword(request.newPassword());
        userService.update(user, context);

        log.info("[userId: {}] Password changed successfully", userId);
        return successResponse(userId);
    }

    private void validateRequest(Request request, Context context) {
        log.info("Validating request");
        userValidator.validateId(request.userId(), context);
        userValidator.validatePassword(request.newPassword(), context);
        if (context.isSuccess()) {
            log.info("Validation success");
        } else {
            log.error("Validation failed");
        }
    }

    private void verifyOldPassword(String inputOldPassword, String storedPassword, Context context) {
        if (!inputOldPassword.equals(storedPassword)) {
            log.error("Old password does not match");
            context.setError(ErrorCode.PASSWORDS_ARE_NOT_EQUAL);
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

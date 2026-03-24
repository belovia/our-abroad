package ru.belov.ourabroad.api.usecases.get.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.get.user.GetUserByEmailUseCase;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.web.validators.UserValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserByEmailUseCaseImpl implements GetUserByEmailUseCase {

    private final UserService userService;
    private final UserValidator userValidator;

    @Override
    public Response execute(Request request) {

        Context context = new Context();
        validateRequest(request, context);
        String userId = request.userId();

        User user = retrieveUserByEmail(request, userId, context);
        log.info("[userId: {}] Found: {}", request.userId(), user);

        if (user == null) {
            log.warn("[userId: {}] Returning error response", request.userId());
            return errorResponse(context);
        }
        log.info("[userId: {}] Returning success response", request.userId());
        return successResponse(user, context);
    }

    protected void validateRequest(Request request, Context context) {
        log.info("Validating request");
        userValidator.validateId(request.userId(), context);
        userValidator.validateEmail(request.email(), context);
        if (context.isSuccess()) {
            log.info("Validation success");
        } else {
            log.error("Validation failed");
        }
    }

    protected User retrieveUserByEmail(Request request, String userId, Context context) {
        if (!context.isSuccess()) {
            log.error("[userId: {}] Error while retrieving user by email", request.userId());
            return null;
        }
        log.info("[userId: {}] Try to find user by email", userId);
        return userService.findByEmail(userId, request.email(), context);
    }

    protected Response successResponse(User user, Context context) {
        return new Response(user, context.isSuccess(), context.getErrorCode().getMessage());
    }

    protected Response errorResponse(Context context) {
        return new Response(null, context.isSuccess(), context.getErrorCode().getMessage());
    }
}


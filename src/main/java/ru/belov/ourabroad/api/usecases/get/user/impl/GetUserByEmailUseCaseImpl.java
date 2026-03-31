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

        User user = retrieveUserByEmail(request, context);
        log.info("[userId: {}] Found: {}", user.getId(), user);

        if (user == null) {
            log.warn("Returning error response");
            return errorResponse(context);
        }
        log.info("Returning success response");
        return successResponse(user, context);
    }

    protected void validateRequest(Request request, Context context) {
        log.info("Validating request");
        userValidator.validateEmail(request.email(), context);
        if (context.isSuccess()) {
            log.info("Validation success");
        } else {
            log.error("Validation failed");
        }
    }

    protected User retrieveUserByEmail(Request request, Context context) {
        if (!context.isSuccess()) {
            log.error("Error while retrieving user by email: {}", request.email());
            return null;
        }
        log.info("Try to find user by email: {}", request.email());
        return userService.findByEmail(request.email(), context);
    }

    protected Response successResponse(User user, Context context) {
        context.setSuccessResult();
        return new Response(user, context.isSuccess(), context.getErrorMessage());
    }

    protected Response errorResponse(Context context) {
        return new Response(null, context.isSuccess(), context.getErrorMessage());
    }
}


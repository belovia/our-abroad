package ru.belov.ourabroad.api.usecases.get.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.get.user.GetUserByIdUsecase;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.web.validators.UserValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserByIdUseCaseImpl implements GetUserByIdUsecase {

    private final UserService userService;
    private final UserValidator userValidator;

    @Override
    public Response execute(Request request) {

        Context context = new Context();
        validateRequest(request, context);
        String userId = request.userId();

        log.info("[userId: {}] Start get user by id", userId);
        User user = retrieveUserById(userId, context);

        if (user == null) {
            log.warn("[userId: {}] Returning error response", request.userId());
            return errorResponse(context);
        }
        log.info("[userId: {}] Returning success response", userId);
        return successResponse(user, context);
    }

    protected void validateRequest(Request request, Context context) {
        log.info("Validating request");
        userValidator.validateId(request.userId(), context);
        if (context.isSuccess()) {
            log.info("Validation success");
        } else {
            log.error("Validation failed");
        }
    }

    private User retrieveUserById(String userId, Context context) {
        if (!context.isSuccess()) {
            log.error("[userId: {}] Error while retrieving user by id", userId);
            return null;
        }
        return userService.findById(userId, context);
    }

    protected Response successResponse(User user, Context context) {
        return new Response(user, context.isSuccess(), context.getErrorCode().getMessage());
    }

    protected Response errorResponse(Context context) {
        return new Response(null, context.isSuccess(), context.getErrorCode().getMessage());
    }
}

package ru.belov.ourabroad.api.usecases.get.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.get.user.GetUserByPhoneUseCase;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.config.security.CurrentUserProvider;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.web.validators.UserValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserByPhoneUseCaseImpl implements GetUserByPhoneUseCase {

    private final UserService userService;
    private final UserValidator userValidator;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public Response execute(Request request) {
        Context context = new Context();
        String userId = currentUserProvider.requiredUserId();
        validateRequest(request, context);

        User user = retrieveUserByPhone(userId, request, context);
        log.info("[userId: {}] Found: {}", userId, user);

        if (user == null) {
            log.warn("[userId: {}] Returning error response", userId);
            return errorResponse(context);
        }
        log.info("[userId: {}] Returning success response", userId);
        return succesResponse(user, context);
    }

    protected void validateRequest(Request request, Context context) {
        log.info("Validating request");
        userValidator.validatePhone(request.phone(), context);
        if (context.isSuccess()) {
            log.info("Validation success");
        } else {
            log.error("Validation failed");
        }
    }

    private User retrieveUserByPhone(String userId, Request request, Context context) {
        if (!context.isSuccess()) {
            log.error("[userId: {}] Error while retrieving user by phone", userId);
            return null;
        }
        log.info("[userId: {}] Try to find user by phone", userId);
        return userService.findByPhone(userId, request.phone(), context);
    }

    private Response errorResponse(Context context) {
        return new Response(null, context.isSuccess(), context.getErrorMessage());
    }

    private Response succesResponse(User foundedUser, Context context) {
        context.setSuccessResult();
        return new Response(foundedUser, context.isSuccess(), context.getErrorMessage());
    }
}

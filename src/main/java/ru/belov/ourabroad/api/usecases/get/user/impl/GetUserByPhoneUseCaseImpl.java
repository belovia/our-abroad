package ru.belov.ourabroad.api.usecases.get.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.get.user.GetUserByPhoneUseCase;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.web.validators.UserValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserByPhoneUseCaseImpl implements GetUserByPhoneUseCase {

    private final UserService userService;
    private final UserValidator userValidator;

    @Override
    public Response execute(Request request) {
        Context context = new Context();
        validateRequest(request, context);

        User user = retrieveUserByPhone(request, context);
        log.info("[userId: {}] Found: {}", request.userId(), user);

        if (user == null) {
            return errorResponse(context);
        }
        log.info("[userId: {}] Returning success response", request.userId());
        return succesResponse(user, context);

    }

    protected void validateRequest(Request request, Context context) {
        log.info("Validating request");
        userValidator.validateId(request.userId(), context);
        userValidator.validatePhone(request.phone(), context);
        if (context.isSuccess()) {
            log.info("Validation success");
        } else {
            log.error("Validation failed");
        }
    }

    private User retrieveUserByPhone(Request request, Context context) {
        if (!context.isSuccess()) {
            log.error("[userId: {}] Error while retrieving user by phone", request.userId());
            return null;
        }
        log.info("[userId: {}] Try to find user by phone", request.userId());
        return userService.findByPhone(request.userId(), request.phone(), context);
    }

    private Response errorResponse(Context context) {
        return new Response(null, context.isSuccess(), context.getErrorCode().getMessage());
    }

    private Response succesResponse(User foundedUser, Context context) {
        return new Response(foundedUser, context.isSuccess(), null);
    }

}

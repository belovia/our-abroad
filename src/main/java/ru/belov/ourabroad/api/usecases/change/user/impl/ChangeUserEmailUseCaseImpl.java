package ru.belov.ourabroad.api.usecases.change.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.AbstractUserUseCase;
import ru.belov.ourabroad.api.usecases.change.user.ChangeUserEmailUseCase;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.poi.storage.UserRepository;
import ru.belov.ourabroad.web.validators.UserValidator;

@Service
@Slf4j
@Transactional
public class ChangeUserEmailUseCaseImpl extends AbstractUserUseCase implements ChangeUserEmailUseCase {

    private final UserValidator userValidator;

    public ChangeUserEmailUseCaseImpl(
            UserRepository userRepository,
            UserValidator userValidator
    ) {
        super(userRepository);
        this.userValidator = userValidator;
    }

    @Override
    public Response execute(Request request) {

        Context context = new Context();
        String userId = request.userId();

        log.info("[userId: {}] Start changing phone", userId);
        validateRequest(request, context);

        User user = findAndUpdateEmailIfExists(userId, request.newEmail(), context);

        if (!context.isSuccess()) {
            return errorResponse(context, userId);
        }

        userRepository.save(user);
        return successResponse(userId);

    }

    protected void validateRequest(Request request, Context context) {
        log.info("Validating request");
        userValidator.validateId(request.userId(), context);
        userValidator.validateEmail(request.newEmail(), context);
        if (context.isSuccess()) {
            log.info("Validation success");
        } else {
            log.error("Validation failed");
        }
    }

    private User findAndUpdateEmailIfExists(String userId, String newEmail, Context context) {
        if (!context.isSuccess()) {
            return null;
        }
        User user = findExistsUser(userId, context);
        user.setEmail(newEmail);
        return user;
    }

    protected Response errorResponse(Context context, String userId) {
        log.error("[userId: {}] Returning error response", userId);
        return new Response(userId, context.isSuccess(), context.getErrorCode().getMessage());
    }

    protected Response successResponse(String userId) {
        log.info("[userId: {}] Returning success response", userId);
        return new Response(userId, true, null);
    }
}

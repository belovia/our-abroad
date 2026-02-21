package ru.belov.ourabroad.api.usecases.change.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.AbstractUserUseCase;
import ru.belov.ourabroad.api.usecases.change.user.ChangeUserPhoneUseCase;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.poi.storage.UserRepository;
import ru.belov.ourabroad.web.validators.UserValidator;

@Service
@Slf4j
@Transactional
public class ChangeUserPhoneUseCaseImpl extends AbstractUserUseCase implements ChangeUserPhoneUseCase {

    private final UserValidator userValidator;

    public ChangeUserPhoneUseCaseImpl(
            UserRepository userRepository,
            UserValidator userValidator
    ) {
        super(userRepository);
        this.userValidator = userValidator;
    }

    @Override
    public Response execute(Request request) {

        Context context = new Context();
        validateRequest(request, context);

        String userId = request.userId();

        log.info("[userId: {}] Start changing phone", userId);

        User user = findExistsUser(userId, context);

        if (!context.isSuccess()) {
            return errorResponse(context, userId);
        }

        updateUser(request, user);

        return successResponse(userId);
    }

    protected void validateRequest(Request request, Context context) {
        log.info("Validating request");
        userValidator.validateId(request.userId(), context);
        userValidator.validatePhone(request.newPhone(), context);
        if (context.isSuccess()) {
            log.info("Validation success");
        } else {
            log.error("Validation failed");
        }
    }

    private void updateUser(Request request, User user) {
        if (user == null) {
            return;
        }
        user.setPhone(request.newPhone());
        userRepository.save(user);
    }

    private Response errorResponse(Context context, String userId) {
        log.error("[userId: {}] Returning error response", userId);
        return new Response(
                userId,
                context.isSuccess(),
                context.getErrorCode().getMessage());
    }

    private Response successResponse(String userId) {
        log.info("[userId: {}] Returning success response", userId);
        return new Response(
                userId,
                true,
                null
        );
    }
}

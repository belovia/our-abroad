package ru.belov.ourabroad.api.usecases.change.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.AbstractUserUseCase;
import ru.belov.ourabroad.api.usecases.change.user.ChangeUserPasswordUseCase;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.poi.storage.UserRepository;
import ru.belov.ourabroad.web.validators.UserValidator;

import static ru.belov.ourabroad.web.validators.ErrorCode.PASSWORDS_ARE_NOT_EQUAL;

@Service
@Slf4j
@Transactional
public class ChangeUserPasswordUseCaseImpl extends AbstractUserUseCase implements ChangeUserPasswordUseCase {

    private final UserValidator userValidator;

    public ChangeUserPasswordUseCaseImpl(
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
        validateInputFields(userId, request.oldPassword(), request.newPassword(), context);

        User user = findExistsUser(userId, context);

        boolean passwordsAreEqual = validateOldPassword(user.getPassword(), request.oldPassword());
        if (passwordsAreEqual) {
            updateUserPassword(request.newPassword(), user);
            return successResponse(userId);
        }

        log.error("Passwords are not equal");
        context.setError(PASSWORDS_ARE_NOT_EQUAL);

        return errorResponse(context, userId);
    }

    private void updateUserPassword(String newPassword, User user) {
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    private void validateInputFields(
            String userId,
            String oldPassword,
            String newPassword,
            Context context
    ) {
        userValidator.validateId(userId, context);
        userValidator.validatePassword(oldPassword, context);
        userValidator.validatePassword(newPassword, context);
    }

    protected boolean validateOldPassword(String passwordFromDb, String passwordFromRequest) {
        return passwordFromDb.equals(passwordFromRequest);
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

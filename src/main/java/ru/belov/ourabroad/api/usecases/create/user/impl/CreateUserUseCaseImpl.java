package ru.belov.ourabroad.api.usecases.create.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.create.user.CreateUserUseCase;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.core.domain.UserFactory;
import ru.belov.ourabroad.poi.storage.UserRepository;
import ru.belov.ourabroad.web.validators.UserValidator;

import java.util.UUID;

import static ru.belov.ourabroad.web.validators.ErrorCode.EMAIL_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateUserUseCaseImpl implements CreateUserUseCase {

    private final UserRepository userRepository;
    private final UserValidator userValidator;

    @Override
    @Transactional
    public Response execute(Request request) {

        Context context = new Context();

        validateRequest(request, context);

        String userId = UUID.randomUUID().toString();
        log.info("[userId: {}] Creating user with id: {}",
                userId,
                userId);

        String email = request.email();

        User user = UserFactory.newUser(
                userId,
                email,
                request.phone(),
                request.password(),
                request.telegramUsername(),
                request.whatsAppNumber(),
                request.activity()
        );

        boolean userEmailExists = checkExistEmail(userId, email);

        if (userEmailExists) {
            log.info("[userId: {}] User already exists with email: {}",
                    userId,
                    email);
            context.setError(EMAIL_ALREADY_EXISTS);
            return errorResponse(context, userId);
        }

        userRepository.save(user);

        return successResponse(userId);
    }


    private void validateRequest(Request request, Context context) {
        userValidator.validateCreateUserRequest(request, context);
    }

    private boolean checkExistEmail(String userId, String email) {
        log.info("[userId: {}] Checking exist email: {}", userId, email);
        return userRepository.existsByEmail(userId, email);
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

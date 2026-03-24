package ru.belov.ourabroad.api.usecases.create.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.create.user.CreateUserUseCase;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.core.domain.UserFactory;
import ru.belov.ourabroad.web.validators.UserValidator;

import java.util.UUID;

import static ru.belov.ourabroad.web.validators.ErrorCode.EMAIL_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateUserUseCaseImpl implements CreateUserUseCase {

    private final UserService userService;
    private final UserValidator userValidator;

    @Override
    @Transactional
    public Response execute(Request request) {
        Context context = new Context();
        String userId = UUID.randomUUID().toString();
        log.info("[userId: {}] Start creating user", userId);

        validateRequest(request, context);
        if (!context.isSuccess()) {
            log.info("[userId: {}] Validation failed", userId);
            return errorResponse(context, userId);
        }

        boolean emailAlreadyExists = checkExistEmail(userId, request.email(), context);
        if (emailAlreadyExists) {
            log.info("[userId: {}] User already exists with email: {}", userId, request.email());
            context.setError(EMAIL_ALREADY_EXISTS);
            return errorResponse(context, userId);
        }

        User user = buildUser(userId, request);
        userService.save(user, context);

        log.info("[userId: {}] User created successfully", userId);
        return successResponse(userId);
    }

    private void validateRequest(Request request, Context context) {
        log.info("Validating request");
        userValidator.validateCreateUserRequest(request, context);
        if (context.isSuccess()) {
            log.info("Validation success");
        } else {
            log.error("Validation failed");
        }
    }

    private boolean checkExistEmail(String userId, String email, Context context) {
        log.info("[userId: {}] Checking if email already exists: {}", userId, email);
        return userService.existsByEmail(userId, email, context);
    }

    private User buildUser(String userId, Request request) {
        return UserFactory.newUser(
                userId,
                request.email(),
                request.phone(),
                request.password(),
                request.telegramUsername(),
                request.whatsAppNumber(),
                request.activity()
        );
    }

    protected Response errorResponse(Context context, String userId) {
        log.error("[userId: {}] Returning error response", userId);
        return new Response(userId, false, context.getErrorCode().getMessage());
    }

    protected Response successResponse(String userId) {
        log.info("[userId: {}] Returning success response", userId);
        return new Response(userId, true, null);
    }
}

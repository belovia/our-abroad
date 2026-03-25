package ru.belov.ourabroad.api.usecases.create.verification.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.create.verification.CreateVerificationUseCase;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.api.usecases.services.verification.VerificationService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.core.domain.Verification;
import ru.belov.ourabroad.core.domain.VerificationFactory;
import ru.belov.ourabroad.core.enums.VerificationType;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.UserValidator;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateVerificationUseCaseImpl implements CreateVerificationUseCase {

    private final UserService userService;
    private final VerificationService verificationService;
    private final UserValidator userValidator;

    @Override
    @Transactional
    public Response execute(Request request) {
        Context context = new Context();
        String userId = request.userId();
        VerificationType type = request.type();
        log.info("[userId: {}] Create verification type {}", userId, type);

        userValidator.validateId(userId, context);
        if (!context.isSuccess()) {
            return errorResponse(context);
        }

        if (type == null) {
            context.setError(ErrorCode.VERIFICATION_TYPE_INVALID);
            return errorResponse(context);
        }

        User user = userService.findById(userId, context);
        if (user == null) {
            return errorResponse(context);
        }

        if (verificationService.hasPendingDuplicate(userId, type, request.relatedEntityId())) {
            context.setError(ErrorCode.VERIFICATION_ALREADY_EXISTS);
            return errorResponse(context);
        }

        String id = UUID.randomUUID().toString();
        Verification verification = VerificationFactory.newVerification(id, userId, type, request.relatedEntityId());
        verificationService.save(verification, context);

        context.setSuccessResult();
        return new Response(id, true, context.getErrorMessage());
    }

    private Response errorResponse(Context context) {
        return new Response(null, false, context.getErrorMessage());
    }
}

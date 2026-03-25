package ru.belov.ourabroad.api.usecases.change.verification.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.change.verification.CompleteVerificationUseCase;
import ru.belov.ourabroad.api.usecases.services.verification.VerificationService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Verification;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.FieldValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompleteVerificationUseCaseImpl implements CompleteVerificationUseCase {

    private final VerificationService verificationService;
    private final FieldValidator fieldValidator;

    @Override
    @Transactional
    public Response execute(Request request) {
        Context context = new Context();
        String verificationId = request.verificationId();
        log.info("[verificationId: {}] Complete verification", verificationId);

        fieldValidator.validateRequiredField(verificationId, context);
        if (!context.isSuccess()) {
            return errorResponse(verificationId, context);
        }

        Verification verification = verificationService.findById(verificationId, context);
        if (verification == null) {
            return errorResponse(verificationId, context);
        }

        if (!verification.isPending()) {
            context.setError(ErrorCode.VERIFICATION_NOT_PENDING);
            return errorResponse(verificationId, context);
        }

        verification.verify();
        verificationService.updateStatus(verification, context);

        context.setSuccessResult();
        return new Response(verificationId, true, context.getErrorMessage());
    }

    private Response errorResponse(String verificationId, Context context) {
        return new Response(verificationId, false, context.getErrorMessage());
    }
}

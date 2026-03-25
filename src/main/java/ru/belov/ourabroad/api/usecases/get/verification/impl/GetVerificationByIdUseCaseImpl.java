package ru.belov.ourabroad.api.usecases.get.verification.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.get.verification.GetVerificationByIdUseCase;
import ru.belov.ourabroad.api.usecases.services.verification.VerificationService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Verification;
import ru.belov.ourabroad.web.validators.FieldValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetVerificationByIdUseCaseImpl implements GetVerificationByIdUseCase {

    private final VerificationService verificationService;
    private final FieldValidator fieldValidator;

    @Override
    public Response execute(Request request) {
        Context context = new Context();
        String verificationId = request.verificationId();
        log.info("[verificationId: {}] Get verification", verificationId);

        fieldValidator.validateRequiredField(verificationId, context);
        if (!context.isSuccess()) {
            return errorResponse(context);
        }

        Verification verification = verificationService.findById(verificationId, context);
        if (verification == null) {
            return errorResponse(context);
        }

        context.setSuccessResult();
        return new Response(verification, true, context.getErrorMessage());
    }

    private Response errorResponse(Context context) {
        return new Response(null, false, context.getErrorMessage());
    }
}

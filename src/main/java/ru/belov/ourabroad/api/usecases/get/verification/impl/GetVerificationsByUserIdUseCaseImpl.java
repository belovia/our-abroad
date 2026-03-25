package ru.belov.ourabroad.api.usecases.get.verification.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.get.verification.GetVerificationsByUserIdUseCase;
import ru.belov.ourabroad.api.usecases.services.verification.VerificationService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Verification;
import ru.belov.ourabroad.web.validators.UserValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetVerificationsByUserIdUseCaseImpl implements GetVerificationsByUserIdUseCase {

    private final VerificationService verificationService;
    private final UserValidator userValidator;

    @Override
    public Response execute(Request request) {
        Context context = new Context();
        String userId = request.userId();
        log.info("[userId: {}] List verifications", userId);

        userValidator.validateId(userId, context);
        if (!context.isSuccess()) {
            return errorResponse(context);
        }

        List<Verification> list = verificationService.findByUserId(userId, context);
        context.setSuccessResult();
        return new Response(list, true, context.getErrorMessage());
    }

    private Response errorResponse(Context context) {
        return new Response(List.of(), false, context.getErrorMessage());
    }
}

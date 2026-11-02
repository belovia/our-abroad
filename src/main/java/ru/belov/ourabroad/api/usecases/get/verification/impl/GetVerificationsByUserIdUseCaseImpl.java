package ru.belov.ourabroad.api.usecases.get.verification.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.get.verification.GetVerificationsByUserIdUseCase;
import ru.belov.ourabroad.api.usecases.services.verification.VerificationService;
import ru.belov.ourabroad.config.security.CurrentUserProvider;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Verification;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetVerificationsByUserIdUseCaseImpl implements GetVerificationsByUserIdUseCase {

    private final VerificationService verificationService;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public Response execute(Request request) {
        Context context = new Context();
        String userId = currentUserProvider.requiredUserId();
        log.info("[userId: {}] List verifications", userId);

        List<Verification> list = verificationService.findByUserId(userId, context);
        context.setSuccessResult();
        return new Response(list, true, context.getErrorMessage());
    }
}

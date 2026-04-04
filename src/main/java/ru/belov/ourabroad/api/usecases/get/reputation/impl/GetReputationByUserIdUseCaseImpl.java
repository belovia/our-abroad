package ru.belov.ourabroad.api.usecases.get.reputation.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.get.reputation.GetReputationByUserIdUseCase;
import ru.belov.ourabroad.api.usecases.services.reputation.ReputationService;
import ru.belov.ourabroad.config.security.CurrentUserProvider;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Reputation;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetReputationByUserIdUseCaseImpl implements GetReputationByUserIdUseCase {

    private final ReputationService reputationService;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public Response execute(Request request) {
        Context context = new Context();
        String userId = currentUserProvider.requiredUserId();
        log.info("[userId: {}] Get reputation", userId);

        Reputation reputation = reputationService.findByUserId(userId, context);
        if (reputation == null) {
            return errorResponse(context);
        }

        context.setSuccessResult();
        return new Response(reputation, true, context.getErrorMessage());
    }

    private Response errorResponse(Context context) {
        return new Response(null, false, context.getErrorMessage());
    }
}

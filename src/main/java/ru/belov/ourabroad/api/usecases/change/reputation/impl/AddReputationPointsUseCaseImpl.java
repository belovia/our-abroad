package ru.belov.ourabroad.api.usecases.change.reputation.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.change.reputation.AddReputationPointsUseCase;
import ru.belov.ourabroad.api.usecases.services.reputation.ReputationService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Reputation;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.UserValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddReputationPointsUseCaseImpl implements AddReputationPointsUseCase {

    private final ReputationService reputationService;
    private final UserValidator userValidator;

    @Override
    @Transactional
    public Response execute(Request request) {
        Context context = new Context();
        String userId = request.userId();
        log.info("[userId: {}] Add reputation points", userId);

        userValidator.validateId(userId, context);
        if (!context.isSuccess()) {
            return errorResponse(userId, context);
        }

        if (request.points() <= 0) {
            context.setError(ErrorCode.REPUTATION_POINTS_INVALID);
            return errorResponse(userId, context);
        }

        Reputation reputation = reputationService.findByUserId(userId, context);
        if (reputation == null) {
            return errorResponse(userId, context);
        }

        reputation.addPoints(request.points());
        reputationService.update(reputation, context);

        context.setSuccessResult();
        return new Response(userId, true, context.getErrorMessage());
    }

    private Response errorResponse(String userId, Context context) {
        return new Response(userId, false, context.getErrorMessage());
    }
}

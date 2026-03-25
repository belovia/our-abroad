package ru.belov.ourabroad.api.usecases.create.reputation.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.create.reputation.CreateReputationUseCase;
import ru.belov.ourabroad.api.usecases.services.reputation.ReputationService;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Reputation;
import ru.belov.ourabroad.core.domain.ReputationFactory;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.poi.storage.ReputationRepository;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.UserValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateReputationUseCaseImpl implements CreateReputationUseCase {

    private final UserService userService;
    private final ReputationService reputationService;
    private final ReputationRepository reputationRepository;
    private final UserValidator userValidator;

    @Override
    @Transactional
    public Response execute(Request request) {
        Context context = new Context();
        String userId = request.userId();
        log.info("[userId: {}] Create reputation", userId);

        userValidator.validateId(userId, context);
        if (!context.isSuccess()) {
            return errorResponse(userId, context);
        }

        User user = userService.findById(userId, context);
        if (user == null) {
            return errorResponse(userId, context);
        }

        if (reputationService.existsByUserId(userId)) {
            context.setError(ErrorCode.REPUTATION_ALREADY_EXISTS);
            return errorResponse(userId, context);
        }

        Reputation reputation = ReputationFactory.initial(userId);
        reputationService.save(reputation, context);

        context.setSuccessResult();
        return new Response(userId, true, context.getErrorMessage());
    }

    private Response errorResponse(String userId, Context context) {
        return new Response(userId, false, context.getErrorMessage());
    }
}

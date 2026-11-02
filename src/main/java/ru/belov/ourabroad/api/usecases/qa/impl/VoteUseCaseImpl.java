package ru.belov.ourabroad.api.usecases.qa.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.qa.VoteUseCase;
import ru.belov.ourabroad.api.usecases.services.qa.VoteApplyResult;
import ru.belov.ourabroad.api.usecases.services.qa.VoteService;
import ru.belov.ourabroad.api.usecases.services.reputation.ReputationService;
import ru.belov.ourabroad.config.security.CurrentUserProvider;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.enums.QaVoteTarget;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.FieldValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteUseCaseImpl implements VoteUseCase {

    private final VoteService voteService;
    private final ReputationService reputationService;
    private final FieldValidator fieldValidator;
    private final CurrentUserProvider currentUserProvider;

    @Override
    @Transactional
    public Response execute(Request request) {
        Context context = new Context();
        String voterUserId = currentUserProvider.requiredUserId();
        log.info("[userId: {}] Vote {} {}", voterUserId, request.target(), request.entityId());

        fieldValidator.validateRequiredField(request.entityId(), context);
        if (context.isSuccess() && (request.target() == null || request.voteType() == null)) {
            context.setError(ErrorCode.REQUEST_VALIDATION_ERROR);
        }
        if (!context.isSuccess()) {
            return errorResponse(context);
        }

        VoteApplyResult result = switch (request.target()) {
            case QUESTION -> voteService.voteQuestion(
                    voterUserId,
                    request.entityId(),
                    request.voteType(),
                    context
            );
            case ANSWER -> voteService.voteAnswer(
                    voterUserId,
                    request.entityId(),
                    request.voteType(),
                    context
            );
        };

        if (result == null || !context.isSuccess()) {
            return errorResponse(context);
        }

        reputationService.addPoints(
                result.contentAuthorId(),
                result.authorReputationDelta(),
                context
        );
        if (!context.isSuccess()) {
            return errorResponse(context);
        }

        context.setSuccessResult();
        return new Response(true, context.getErrorMessage());
    }

    private Response errorResponse(Context context) {
        return new Response(false, context.getErrorMessage());
    }
}

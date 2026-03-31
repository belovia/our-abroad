package ru.belov.ourabroad.api.usecases.qa.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.qa.QaReputationRules;
import ru.belov.ourabroad.api.usecases.services.qa.AcceptAnswerResult;
import ru.belov.ourabroad.api.usecases.services.qa.AnswerService;
import ru.belov.ourabroad.api.usecases.services.reputation.ReputationService;
import ru.belov.ourabroad.core.domain.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class AcceptAnswerUseCaseImpl {

    private final AnswerService answerService;
    private final ReputationService reputationService;

    @Transactional
    public void execute(String answerId, String userId, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        log.info("[answerId: {}][userId: {}] Accept answer", answerId, userId);

        AcceptAnswerResult result = answerService.acceptAnswer(answerId, userId, context);
        if (!context.isSuccess() || result == null) {
            return;
        }
        if (!result.changed()) {
            context.setSuccessResult();
            return;
        }

        reputationService.addPoints(
                result.answerAuthorId(),
                QaReputationRules.POINTS_ACCEPTED_ANSWER_AUTHOR,
                context
        );
        if (!context.isSuccess()) {
            return;
        }
        reputationService.addPoints(
                result.questionAuthorId(),
                QaReputationRules.POINTS_ACCEPTED_ANSWER_QUESTION_AUTHOR,
                context
        );
        if (!context.isSuccess()) {
            return;
        }

        context.setSuccessResult();
    }
}


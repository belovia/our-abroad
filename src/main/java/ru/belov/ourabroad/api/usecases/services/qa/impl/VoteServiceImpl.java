package ru.belov.ourabroad.api.usecases.services.qa.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.api.usecases.services.qa.AnswerService;
import ru.belov.ourabroad.api.usecases.services.qa.QuestionService;
import ru.belov.ourabroad.api.usecases.services.qa.VoteApplyResult;
import ru.belov.ourabroad.api.usecases.services.qa.VotePersistenceService;
import ru.belov.ourabroad.api.usecases.services.qa.VoteService;
import ru.belov.ourabroad.core.domain.Answer;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Question;
import ru.belov.ourabroad.core.domain.Vote;
import ru.belov.ourabroad.core.enums.VoteType;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class VoteServiceImpl implements VoteService {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final VotePersistenceService votePersistence;

    @Override
    public VoteApplyResult voteQuestion(String voterUserId, String questionId, VoteType voteType, Context context) {
        if (!context.isSuccess()) {
            return null;
        }
        Question question = questionService.findByIdOrError(questionId, context);
        if (!context.isSuccess() || question == null) {
            return null;
        }
        return applyVote(voterUserId, questionId, voteType, context, question.getAuthorId(), true);
    }

    @Override
    public VoteApplyResult voteAnswer(String voterUserId, String answerId, VoteType voteType, Context context) {
        if (!context.isSuccess()) {
            return null;
        }
        Answer answer = answerService.findByIdOrError(answerId, context);
        if (!context.isSuccess() || answer == null) {
            return null;
        }
        return applyVote(voterUserId, answerId, voteType, context, answer.getAuthorId(), false);
    }

    private VoteApplyResult applyVote(
            String voterUserId,
            String entityId,
            VoteType newType,
            Context context,
            String contentAuthorId,
            boolean questionTarget
    ) {
        if (!context.isSuccess()) {
            return null;
        }

        Vote existing = votePersistence.findVote(voterUserId, entityId, context);
        if (!context.isSuccess()) {
            return null;
        }

        if (existing != null && existing.getType() == newType) {
            log.info("[userId: {}][entityId: {}] Same vote — no-op", voterUserId, entityId);
            return new VoteApplyResult(contentAuthorId, 0);
        }

        int prevScoreContrib = existing != null ? scoreContribution(existing.getType()) : 0;
        int prevUpBonus = existing != null ? upvoteBonus(existing.getType()) : 0;
        int newScoreContrib = scoreContribution(newType);
        int entityDelta = existing == null ? newScoreContrib : (newScoreContrib - prevScoreContrib);
        int newUpBonus = upvoteBonus(newType);
        int authorReputationDelta = newUpBonus - prevUpBonus;

        if (existing == null) {
            Vote created = Vote.create(UUID.randomUUID().toString(), voterUserId, entityId, newType);
            votePersistence.createVote(created, context);
        } else {
            existing.setType(newType);
            votePersistence.updateVote(existing, context);
        }

        if (!context.isSuccess()) {
            return null;
        }

        if (questionTarget) {
            votePersistence.applyVoteDeltaToQuestion(entityId, entityDelta, context);
        } else {
            votePersistence.applyVoteDeltaToAnswer(entityId, entityDelta, context);
        }

        if (!context.isSuccess()) {
            return null;
        }

        return new VoteApplyResult(contentAuthorId, authorReputationDelta);
    }

    private static int scoreContribution(VoteType type) {
        return type == VoteType.UP ? 1 : -1;
    }

    private static int upvoteBonus(VoteType type) {
        return type == VoteType.UP ? 1 : 0;
    }
}

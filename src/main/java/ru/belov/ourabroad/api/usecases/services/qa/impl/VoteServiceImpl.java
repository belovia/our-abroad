package ru.belov.ourabroad.api.usecases.services.qa.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.api.usecases.services.qa.VoteApplyResult;
import ru.belov.ourabroad.api.usecases.services.qa.VoteService;
import ru.belov.ourabroad.core.domain.Answer;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Question;
import ru.belov.ourabroad.core.domain.Vote;
import ru.belov.ourabroad.core.enums.VoteType;
import ru.belov.ourabroad.poi.storage.AnswerRepository;
import ru.belov.ourabroad.poi.storage.QuestionRepository;
import ru.belov.ourabroad.poi.storage.VoteRepository;

import java.util.Optional;
import java.util.UUID;

import static ru.belov.ourabroad.web.validators.ErrorCode.ANSWER_NOT_FOUND;
import static ru.belov.ourabroad.web.validators.ErrorCode.QUESTION_NOT_FOUND;

@Component
@RequiredArgsConstructor
@Slf4j
public class VoteServiceImpl implements VoteService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final VoteRepository voteRepository;

    @Override
    public VoteApplyResult voteQuestion(String voterUserId, String questionId, VoteType voteType, Context context) {
        if (!context.isSuccess()) {
            return null;
        }
        Optional<Question> questionOpt = questionRepository.findById(questionId);
        if (questionOpt.isEmpty()) {
            log.warn("[questionId: {}] Question not found for vote", questionId);
            context.setError(QUESTION_NOT_FOUND);
            return null;
        }
        Question question = questionOpt.get();
        return applyVote(
                voterUserId,
                questionId,
                voteType,
                context,
                question.getAuthorId(),
                delta -> questionRepository.addVoteDelta(questionId, delta)
        );
    }

    @Override
    public VoteApplyResult voteAnswer(String voterUserId, String answerId, VoteType voteType, Context context) {
        if (!context.isSuccess()) {
            return null;
        }
        Optional<Answer> answerOpt = answerRepository.findById(answerId);
        if (answerOpt.isEmpty()) {
            log.warn("[answerId: {}] Answer not found for vote", answerId);
            context.setError(ANSWER_NOT_FOUND);
            return null;
        }
        Answer answer = answerOpt.get();
        return applyVote(
                voterUserId,
                answerId,
                voteType,
                context,
                answer.getAuthorId(),
                delta -> answerRepository.addVoteDelta(answerId, delta)
        );
    }

    private VoteApplyResult applyVote(
            String voterUserId,
            String entityId,
            VoteType newType,
            Context context,
            String contentAuthorId,
            EntityVoteDeltaApplier applier
    ) {
        Optional<Vote> existingOpt = voteRepository.findByUserIdAndEntityId(voterUserId, entityId);
        if (existingOpt.isPresent() && existingOpt.get().getType() == newType) {
            log.info("[userId: {}][entityId: {}] Same vote — no-op", voterUserId, entityId);
            return new VoteApplyResult(contentAuthorId, 0);
        }

        int prevScoreContrib = existingOpt.map(v -> scoreContribution(v.getType())).orElse(0);
        int prevUpBonus = existingOpt.map(v -> upvoteBonus(v.getType())).orElse(0);
        int newScoreContrib = scoreContribution(newType);
        int entityDelta = existingOpt.isEmpty() ? newScoreContrib : (newScoreContrib - prevScoreContrib);
        int newUpBonus = upvoteBonus(newType);
        int authorReputationDelta = newUpBonus - prevUpBonus;

        if (existingOpt.isEmpty()) {
            Vote created = Vote.create(UUID.randomUUID().toString(), voterUserId, entityId, newType);
            voteRepository.save(created);
            log.info("[userId: {}][entityId: {}] Created vote {}", voterUserId, entityId, newType);
        } else {
            Vote vote = existingOpt.get();
            vote.setType(newType);
            voteRepository.updateType(vote);
            log.info("[userId: {}][entityId: {}] Updated vote to {}", voterUserId, entityId, newType);
        }

        if (!applier.addDelta(entityDelta)) {
            log.warn("[entityId: {}] Vote counter update affected no rows", entityId);
        }

        return new VoteApplyResult(contentAuthorId, authorReputationDelta);
    }

    private static int scoreContribution(VoteType type) {
        return type == VoteType.UP ? 1 : -1;
    }

    private static int upvoteBonus(VoteType type) {
        return type == VoteType.UP ? 1 : 0;
    }

    @FunctionalInterface
    private interface EntityVoteDeltaApplier {
        boolean addDelta(int delta);
    }
}

package ru.belov.ourabroad.api.usecases.services.qa.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.api.usecases.services.qa.AnswerService;
import ru.belov.ourabroad.api.usecases.services.qa.QuestionService;
import ru.belov.ourabroad.api.usecases.services.qa.VotePersistenceService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Vote;
import ru.belov.ourabroad.poi.storage.VoteRepository;

import static ru.belov.ourabroad.web.validators.ErrorCode.VOTE_UPDATE_FAILED;

@Component
@RequiredArgsConstructor
@Slf4j
public class VotePersistenceServiceImpl implements VotePersistenceService {

    private final VoteRepository voteRepository;
    private final QuestionService questionService;
    private final AnswerService answerService;

    @Override
    public Vote findVote(String userId, String entityId, Context context) {
        if (!context.isSuccess()) {
            return null;
        }
        return voteRepository.findByUserIdAndEntityId(userId, entityId).orElse(null);
    }

    @Override
    public void createVote(Vote vote, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        log.info("[userId: {}][entityId: {}] Persist new vote {}", vote.getUserId(), vote.getEntityId(), vote.getType());
        if (!voteRepository.save(vote)) {
            log.warn("[userId: {}][entityId: {}] Vote insert affected no rows", vote.getUserId(), vote.getEntityId());
            context.setError(VOTE_UPDATE_FAILED);
        }
    }

    @Override
    public void updateVote(Vote vote, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        log.info("[voteId: {}] Persist vote type {}", vote.getId(), vote.getType());
        if (!voteRepository.updateType(vote)) {
            log.warn("[voteId: {}] Vote update affected no rows", vote.getId());
            context.setError(VOTE_UPDATE_FAILED);
        }
    }

    @Override
    public void applyVoteDeltaToQuestion(String questionId, int delta, Context context) {
        questionService.applyVoteDelta(questionId, delta, context);
    }

    @Override
    public void applyVoteDeltaToAnswer(String answerId, int delta, Context context) {
        answerService.applyVoteDelta(answerId, delta, context);
    }
}

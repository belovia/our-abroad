package ru.belov.ourabroad.api.usecases.services.qa;

import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Vote;

/**
 * Инкапсулирует работу с хранилищем голосов (таблица qa_votes).
 */
public interface VotePersistenceService {

    /**
     * @return существующий голос или {@code null}, если записи нет (это не ошибка)
     */
    Vote findVote(String userId, String entityId, Context context);

    void createVote(Vote vote, Context context);

    void updateVote(Vote vote, Context context);

    void applyVoteDeltaToQuestion(String questionId, int delta, Context context);

    void applyVoteDeltaToAnswer(String answerId, int delta, Context context);
}

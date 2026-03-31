package ru.belov.ourabroad.api.usecases.services.qa;

import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Question;

public interface QuestionService {

    void createQuestion(Question question, Context context);

    Question findById(String questionId, Context context);

    /**
     * Загрузка вопроса для сценариев, где отсутствие записи — ошибка (аналог {@link #findById}).
     */
    Question findByIdOrError(String questionId, Context context);

    void incrementAnswersCount(String questionId, Context context);

    void applyVoteDelta(String questionId, int delta, Context context);
}

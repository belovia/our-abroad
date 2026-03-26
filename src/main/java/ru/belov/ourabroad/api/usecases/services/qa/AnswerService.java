package ru.belov.ourabroad.api.usecases.services.qa;

import ru.belov.ourabroad.core.domain.Answer;
import ru.belov.ourabroad.core.domain.Context;

import java.util.List;

public interface AnswerService {

    void createAnswer(Answer answer, Context context);

    List<Answer> findByQuestionId(String questionId, Context context);

    Answer findById(String answerId, Context context);

    AcceptAnswerResult acceptAnswer(String answerId, String userId, Context context);
}

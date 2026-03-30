package ru.belov.ourabroad.api.usecases.services.qa.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.api.usecases.services.qa.QuestionService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Question;
import ru.belov.ourabroad.poi.storage.QuestionRepository;

import java.util.Optional;

import static ru.belov.ourabroad.web.validators.ErrorCode.ENTITY_VOTE_UPDATE_FAILED;
import static ru.belov.ourabroad.web.validators.ErrorCode.QUESTION_NOT_FOUND;

@Component
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    @Override
    public void createQuestion(Question question, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        log.info("[questionId: {}] Saving question", question.getId());
        questionRepository.save(question);
    }

    @Override
    public Question findById(String questionId, Context context) {
        if (!context.isSuccess()) {
            return null;
        }
        log.info("[questionId: {}] Try to load question", questionId);
        Optional<Question> fromDb = questionRepository.findById(questionId);
        if (fromDb.isEmpty()) {
            log.warn("[questionId: {}] Question not found", questionId);
            context.setError(QUESTION_NOT_FOUND);
            return null;
        }
        return fromDb.get();
    }

    @Override
    public Question findByIdOrError(String questionId, Context context) {
        return findById(questionId, context);
    }

    @Override
    public void incrementAnswersCount(String questionId, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        log.info("[questionId: {}] Increment answers count", questionId);
        if (!questionRepository.incrementAnswersCount(questionId)) {
            log.warn("[questionId: {}] incrementAnswersCount affected no rows", questionId);
            context.setError(QUESTION_NOT_FOUND);
        }
    }

    @Override
    public void applyVoteDelta(String questionId, int delta, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        log.info("[questionId: {}] Apply vote delta {}", questionId, delta);
        if (!questionRepository.addVoteDelta(questionId, delta)) {
            log.warn("[questionId: {}] addVoteDelta affected no rows", questionId);
            context.setError(ENTITY_VOTE_UPDATE_FAILED);
        }
    }
}

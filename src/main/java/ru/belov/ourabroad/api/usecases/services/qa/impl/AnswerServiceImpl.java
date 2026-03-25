package ru.belov.ourabroad.api.usecases.services.qa.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.api.usecases.services.qa.AnswerService;
import ru.belov.ourabroad.core.domain.Answer;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.poi.storage.AnswerRepository;

import java.util.List;
import java.util.Optional;

import static ru.belov.ourabroad.web.validators.ErrorCode.ANSWER_NOT_FOUND;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;

    @Override
    public void createAnswer(Answer answer, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        log.info("[answerId: {}] Saving answer for question {}", answer.getId(), answer.getQuestionId());
        answerRepository.save(answer);
    }

    @Override
    public List<Answer> findByQuestionId(String questionId, Context context) {
        if (!context.isSuccess()) {
            return List.of();
        }
        log.info("[questionId: {}] Load answers", questionId);
        return answerRepository.findByQuestionId(questionId);
    }

    @Override
    public Answer findById(String answerId, Context context) {
        if (!context.isSuccess()) {
            return null;
        }
        log.info("[answerId: {}] Try to load answer", answerId);
        Optional<Answer> fromDb = answerRepository.findById(answerId);
        if (fromDb.isEmpty()) {
            log.warn("[answerId: {}] Answer not found", answerId);
            context.setError(ANSWER_NOT_FOUND);
            return null;
        }
        return fromDb.get();
    }
}

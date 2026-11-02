package ru.belov.ourabroad.api.usecases.qa.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.qa.AnswerQuestionUseCase;
import ru.belov.ourabroad.api.usecases.qa.QaReputationRules;
import ru.belov.ourabroad.api.usecases.services.qa.AnswerService;
import ru.belov.ourabroad.api.usecases.services.qa.QuestionService;
import ru.belov.ourabroad.api.usecases.services.reputation.ReputationService;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.config.security.CurrentUserProvider;
import ru.belov.ourabroad.core.domain.Answer;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Question;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.web.validators.FieldValidator;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnswerQuestionUseCaseImpl implements AnswerQuestionUseCase {

    private final UserService userService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final ReputationService reputationService;
    private final FieldValidator fieldValidator;
    private final CurrentUserProvider currentUserProvider;

    @Override
    @Transactional
    public Response execute(Request request) {
        Context context = new Context();
        String questionId = request.questionId();
        String authorId = currentUserProvider.requiredUserId();
        log.info("[questionId: {}][userId: {}] Answer question", questionId, authorId);

        fieldValidator.validateRequiredField(questionId, context);
        fieldValidator.validateRequiredField(request.content(), context);
        if (!context.isSuccess()) {
            return errorResponse(questionId, context);
        }

        User author = userService.findById(authorId, context);
        if (author == null) {
            return errorResponse(questionId, context);
        }

        Question question = questionService.findById(questionId, context);
        if (question == null) {
            return errorResponse(questionId, context);
        }

        String answerId = UUID.randomUUID().toString();
        Answer answer = Answer.create(
                answerId,
                questionId,
                authorId,
                request.specialistProfileId(),
                request.content().trim(),
                0,
                false,
                null
        );

        answerService.createAnswer(answer, context);
        if (!context.isSuccess()) {
            return errorResponse(questionId, context);
        }

        questionService.incrementAnswersCount(questionId, context);
        if (!context.isSuccess()) {
            return errorResponse(questionId, context);
        }

        reputationService.addPoints(authorId, QaReputationRules.POINTS_NEW_ANSWER, context);
        if (!context.isSuccess()) {
            return errorResponse(questionId, context);
        }

        context.setSuccessResult();
        return new Response(answerId, questionId, true, context.getErrorMessage());
    }

    private Response errorResponse(String questionId, Context context) {
        return new Response(null, questionId, false, context.getErrorMessage());
    }
}

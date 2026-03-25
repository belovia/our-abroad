package ru.belov.ourabroad.api.usecases.qa.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.qa.CreateQuestionUseCase;
import ru.belov.ourabroad.api.usecases.qa.QaReputationRules;
import ru.belov.ourabroad.api.usecases.services.qa.QuestionService;
import ru.belov.ourabroad.api.usecases.services.reputation.ReputationService;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Question;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.web.validators.FieldValidator;
import ru.belov.ourabroad.web.validators.UserValidator;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateQuestionUseCaseImpl implements CreateQuestionUseCase {

    private final UserService userService;
    private final QuestionService questionService;
    private final ReputationService reputationService;
    private final UserValidator userValidator;
    private final FieldValidator fieldValidator;

    @Override
    @Transactional
    public Response execute(Request request) {
        Context context = new Context();
        String authorId = request.authorId();
        log.info("[userId: {}] Create question", authorId);

        validate(request, context);
        if (!context.isSuccess()) {
            return errorResponse(context);
        }

        User author = userService.findById(authorId, context);
        if (author == null) {
            return errorResponse(context);
        }

        String questionId = UUID.randomUUID().toString();
        Question question = Question.create(
                questionId,
                authorId,
                request.title().trim(),
                request.content().trim(),
                request.tags(),
                0,
                0,
                null
        );

        questionService.createQuestion(question, context);
        if (!context.isSuccess()) {
            return errorResponse(context);
        }

        reputationService.addPoints(authorId, QaReputationRules.POINTS_NEW_QUESTION, context);
        if (!context.isSuccess()) {
            return errorResponse(context);
        }

        context.setSuccessResult();
        return new Response(questionId, true, context.getErrorMessage());
    }

    private void validate(Request request, Context context) {
        userValidator.validateId(request.authorId(), context);
        fieldValidator.validateRequiredField(request.title(), context);
        fieldValidator.validateRequiredField(request.content(), context);
    }

    private Response errorResponse(Context context) {
        return new Response(null, false, context.getErrorMessage());
    }
}

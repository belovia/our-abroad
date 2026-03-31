package ru.belov.ourabroad.api.usecases.qa.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.qa.GetQuestionByIdUseCase;
import ru.belov.ourabroad.api.usecases.services.qa.QuestionService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Question;
import ru.belov.ourabroad.web.dto.qa.read.QuestionResponse;
import ru.belov.ourabroad.web.validators.FieldValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetQuestionByIdUseCaseImpl implements GetQuestionByIdUseCase {

    private final QuestionService questionService;
    private final FieldValidator fieldValidator;

    @Override
    @Transactional(readOnly = true)
    public Response execute(Request request) {
        Context context = new Context();
        fieldValidator.validateRequest(request, context);
        if (!context.isSuccess()) {
            return errorResponse(context);
        }
        fieldValidator.validateRequiredField(request.questionId(), context);
        if (!context.isSuccess()) {
            return errorResponse(context);
        }

        log.info("[questionId: {}] Get question", request.questionId());
        Question q = questionService.findByIdOrError(request.questionId(), context);
        if (!context.isSuccess() || q == null) {
            return errorResponse(context);
        }

        context.setSuccessResult();
        return new Response(
                new QuestionResponse(
                        q.getId(),
                        q.getAuthorId(),
                        q.getTitle(),
                        q.getContent(),
                        q.getTags(),
                        q.getVotes(),
                        q.getAnswersCount(),
                        q.getCreatedAt()
                ),
                true,
                context.getErrorMessage()
        );
    }

    private static Response errorResponse(Context context) {
        return new Response(null, false, context.getErrorMessage());
    }
}


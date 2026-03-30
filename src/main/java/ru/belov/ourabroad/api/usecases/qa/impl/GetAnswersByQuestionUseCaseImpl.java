package ru.belov.ourabroad.api.usecases.qa.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.qa.GetAnswersByQuestionUseCase;
import ru.belov.ourabroad.api.usecases.services.qa.AnswerService;
import ru.belov.ourabroad.api.usecases.services.qa.QuestionService;
import ru.belov.ourabroad.core.domain.Answer;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Question;
import ru.belov.ourabroad.web.dto.qa.read.AnswerResponse;
import ru.belov.ourabroad.web.validators.FieldValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAnswersByQuestionUseCaseImpl implements GetAnswersByQuestionUseCase {

    private final QuestionService questionService;
    private final AnswerService answerService;
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

        log.info("[questionId: {}] Get answers", request.questionId());
        Question q = questionService.findByIdOrError(request.questionId(), context);
        if (!context.isSuccess() || q == null) {
            return errorResponse(context);
        }

        List<Answer> answers = answerService.getAnswersSorted(request.questionId(), context);
        if (!context.isSuccess()) {
            return errorResponse(context);
        }

        context.setSuccessResult();
        return new Response(
                answers.stream().map(GetAnswersByQuestionUseCaseImpl::toResponse).toList(),
                true,
                context.getErrorMessage()
        );
    }

    private static AnswerResponse toResponse(Answer a) {
        return new AnswerResponse(
                a.getId(),
                a.getContent(),
                a.getVotes(),
                a.isAccepted(),
                a.getCreatedAt(),
                a.getSpecialistProfileId()
        );
    }

    private static Response errorResponse(Context context) {
        return new Response(List.of(), false, context.getErrorMessage());
    }
}


package ru.belov.ourabroad.api.usecases.qa.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.qa.GetQuestionsUseCase;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Question;
import ru.belov.ourabroad.poi.storage.QuestionRepository;
import ru.belov.ourabroad.web.dto.qa.read.QuestionResponse;

import java.util.List;

import static ru.belov.ourabroad.web.validators.ErrorCode.REQUEST_VALIDATION_ERROR;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetQuestionsUseCaseImpl implements GetQuestionsUseCase {

    private final QuestionRepository questionRepository;

    @Override
    @Transactional(readOnly = true)
    public Response execute(Request request) {
        Context context = new Context();
        if (request == null) {
            context.setError(REQUEST_VALIDATION_ERROR);
            return errorResponse(context);
        }

        Pageable pageable = toPageable(request.page(), request.size());
        Sort sort = toSort(request.sort());
        log.info("[page: {}][size: {}][sort: {}][tag: {}] Get questions", request.page(), request.size(), request.sort(), request.tag());

        List<Question> questions = request.tag() == null || request.tag().isBlank()
                ? questionRepository.findAll(pageable, sort)
                : questionRepository.findByTag(request.tag(), pageable, sort);

        context.setSuccessResult();
        return new Response(
                questions.stream().map(GetQuestionsUseCaseImpl::toResponse).toList(),
                true,
                context.getErrorMessage()
        );
    }

    private static QuestionResponse toResponse(Question q) {
        return new QuestionResponse(
                q.getId(),
                q.getAuthorId(),
                q.getTitle(),
                q.getContent(),
                q.getTags(),
                q.getVotes(),
                q.getAnswersCount(),
                q.getCreatedAt()
        );
    }

    private static Pageable toPageable(Integer page, Integer size) {
        if (page == null || size == null) {
            return Pageable.unpaged();
        }
        if (page < 0 || size <= 0) {
            return Pageable.unpaged();
        }
        return PageRequest.of(page, size);
    }

    private static Sort toSort(SortMode mode) {
        if (mode == null) {
            return Sort.by(Sort.Order.desc("createdAt"));
        }
        return switch (mode) {
            case NEWEST -> Sort.by(Sort.Order.desc("createdAt"));
            case VOTES -> Sort.by(Sort.Order.desc("votes"));
        };
    }

    private static Response errorResponse(Context context) {
        return new Response(List.of(), false, context.getErrorMessage());
    }
}


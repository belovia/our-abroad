package ru.belov.ourabroad.api.usecases.comments.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.comments.GetCommentsUseCase;
import ru.belov.ourabroad.api.usecases.services.comments.CommentService;
import ru.belov.ourabroad.api.usecases.services.comments.CommentThreadView;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.enums.CommentEntityType;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.FieldValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetCommentsUseCaseImpl implements GetCommentsUseCase {

    private final CommentService commentService;
    private final FieldValidator fieldValidator;

    @Override
    @Transactional(readOnly = true)
    public Response execute(Request request) {
        Context context = new Context();
        log.info(
                "[entityId: {}][entityType: {}] Get comments",
                request.entityId(),
                request.entityType()
        );

        fieldValidator.validateRequiredField(request.entityId(), context);
        fieldValidator.validateRequiredField(request.entityType(), context);
        CommentEntityType entityType = null;
        if (context.isSuccess()) {
            entityType = CommentEntityType.parse(request.entityType()).orElse(null);
            if (entityType == null) {
                context.setError(ErrorCode.REQUEST_VALIDATION_ERROR);
            }
        }
        if (!context.isSuccess()) {
            return errorResponse(context);
        }

        Sort sort = request.sort() != null ? request.sort() : Sort.by(Sort.Order.desc("createdAt"));
        List<CommentThreadView> threads = commentService.getCommentThreads(
                request.entityId(),
                entityType,
                sort,
                request.pageable(),
                context
        );
        if (!context.isSuccess()) {
            return errorResponse(context);
        }

        context.setSuccessResult();
        return new Response(threads, true, context.getErrorMessage());
    }

    private static Response errorResponse(Context context) {
        return new Response(List.of(), false, context.getErrorMessage());
    }
}

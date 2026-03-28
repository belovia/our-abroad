package ru.belov.ourabroad.api.usecases.comments.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.comments.CreateCommentUseCase;
import ru.belov.ourabroad.api.usecases.services.comments.CommentService;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.core.enums.CommentEntityType;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.FieldValidator;
import ru.belov.ourabroad.web.validators.UserValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateCommentUseCaseImpl implements CreateCommentUseCase {

    private final UserService userService;
    private final CommentService commentService;
    private final UserValidator userValidator;
    private final FieldValidator fieldValidator;

    @Override
    @Transactional
    public Response execute(Request request) {
        Context context = new Context();
        log.info(
                "[userId: {}][entityId: {}][entityType: {}] Create comment",
                request.authorId(),
                request.entityId(),
                request.entityType()
        );

        userValidator.validateId(request.authorId(), context);
        fieldValidator.validateRequiredField(request.entityId(), context);
        fieldValidator.validateRequiredField(request.content(), context);
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

        User author = userService.findById(request.authorId(), context);
        if (author == null) {
            return errorResponse(context);
        }

        String commentId = commentService.createComment(
                request.authorId(),
                request.entityId(),
                entityType,
                request.content(),
                request.parentId(),
                context
        );
        if (!context.isSuccess() || commentId == null) {
            return errorResponse(context);
        }

        context.setSuccessResult();
        return new Response(commentId, true, context.getErrorMessage());
    }

    private static Response errorResponse(Context context) {
        return new Response(null, false, context.getErrorMessage());
    }
}

package ru.belov.ourabroad.api.usecases.comments.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.comments.LikeCommentUseCase;
import ru.belov.ourabroad.api.usecases.services.comments.CommentLikeService;
import ru.belov.ourabroad.api.usecases.services.comments.CommentLikeToggleResult;
import ru.belov.ourabroad.config.security.CurrentUserProvider;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.web.validators.FieldValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeCommentUseCaseImpl implements LikeCommentUseCase {

    private final CommentLikeService commentLikeService;
    private final FieldValidator fieldValidator;
    private final CurrentUserProvider currentUserProvider;

    @Override
    @Transactional
    public Response execute(Request request) {
        Context context = new Context();
        String userId = currentUserProvider.requiredUserId();
        log.info("[commentId: {}][userId: {}] Like comment", request.commentId(), userId);

        fieldValidator.validateRequiredField(request.commentId(), context);
        if (!context.isSuccess()) {
            return errorResponse(context);
        }

        CommentLikeToggleResult result = commentLikeService.likeComment(
                userId,
                request.commentId(),
                context
        );
        if (!context.isSuccess() || result == null) {
            return errorResponse(context);
        }

        context.setSuccessResult();
        return new Response(result.likedAfter(), true, context.getErrorMessage());
    }

    private static Response errorResponse(Context context) {
        return new Response(false, false, context.getErrorMessage());
    }
}

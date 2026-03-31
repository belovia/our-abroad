package ru.belov.ourabroad.api.usecases.services.comments.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.services.comments.CommentLikeService;
import ru.belov.ourabroad.api.usecases.services.comments.CommentLikeToggleResult;
import ru.belov.ourabroad.core.domain.CommentLike;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.poi.storage.CommentLikeRepository;
import ru.belov.ourabroad.poi.storage.CommentRepository;

import java.util.Optional;

import static ru.belov.ourabroad.web.validators.ErrorCode.COMMENT_NOT_FOUND;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentLikeServiceImpl implements CommentLikeService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    @Override
    @Transactional
    public CommentLikeToggleResult likeComment(String userId, String commentId, Context context) {
        if (!context.isSuccess()) {
            return null;
        }
        log.info("[commentId: {}][userId: {}] Toggle comment like", commentId, userId);

        if (commentRepository.findById(commentId).isEmpty()) {
            log.warn("[commentId: {}] Comment not found for like", commentId);
            context.setError(COMMENT_NOT_FOUND);
            return null;
        }

        Optional<CommentLike> existing = commentLikeRepository.findByUserIdAndCommentId(userId, commentId);
        if (existing.isEmpty()) {
            commentLikeRepository.save(CommentLike.create(userId, commentId));
            if (!commentRepository.updateLikes(commentId, 1)) {
                log.warn("[commentId: {}] updateLikes(+1) affected no rows", commentId);
                context.setError(COMMENT_NOT_FOUND);
                return null;
            }
            log.info("[commentId: {}][userId: {}] Like created", commentId, userId);
            return new CommentLikeToggleResult(true);
        }

        commentLikeRepository.deleteByUserIdAndCommentId(userId, commentId);
        if (!commentRepository.updateLikes(commentId, -1)) {
            log.warn("[commentId: {}] updateLikes(-1) affected no rows", commentId);
            context.setError(COMMENT_NOT_FOUND);
            return null;
        }
        log.info("[commentId: {}][userId: {}] Like removed", commentId, userId);
        return new CommentLikeToggleResult(false);
    }
}

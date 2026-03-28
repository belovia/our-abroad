package ru.belov.ourabroad.poi.storage;

import ru.belov.ourabroad.core.domain.CommentLike;

import java.util.Optional;

public interface CommentLikeRepository {

    Optional<CommentLike> findByUserIdAndCommentId(String userId, String commentId);

    void save(CommentLike like);

    void deleteByUserIdAndCommentId(String userId, String commentId);
}

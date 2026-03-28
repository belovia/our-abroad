package ru.belov.ourabroad.core.domain;

import java.util.Objects;
import java.util.UUID;

public record CommentLike(
        String id,
        String userId,
        String commentId
) {

    public CommentLike {
        Objects.requireNonNull(id);
        Objects.requireNonNull(userId);
        Objects.requireNonNull(commentId);
    }

    public static CommentLike create(String userId, String commentId) {
        return new CommentLike(UUID.randomUUID().toString(), userId, commentId);
    }
}

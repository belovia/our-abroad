package ru.belov.ourabroad.web.dto.comment;

public record CreateCommentBody(
        String authorId,
        String entityId,
        String entityType,
        String content,
        String parentId
) {
}

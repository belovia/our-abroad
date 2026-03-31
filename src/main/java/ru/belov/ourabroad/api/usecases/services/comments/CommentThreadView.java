package ru.belov.ourabroad.api.usecases.services.comments;

import ru.belov.ourabroad.core.domain.Comment;

import java.util.List;

/**
 * Корневой комментарий и прямые ответы (не более одного уровня вложенности от корня).
 */
public record CommentThreadView(Comment root, List<Comment> replies) {
}

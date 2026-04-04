package ru.belov.ourabroad.api.usecases.comments;

public interface CreateCommentUseCase {

    Response execute(Request request);

    record Request(
            String entityId,
            String entityType,
            String content,
            String parentId
    ) {
    }

    record Response(String commentId, boolean success, String errorMessage) {
    }
}

package ru.belov.ourabroad.api.usecases.comments;

public interface LikeCommentUseCase {

    Response execute(Request request);

    record Request(String commentId) {
    }

    /**
     * @param liked {@code true}, если после запроса лайк активен
     */
    record Response(boolean liked, boolean success, String errorMessage) {
    }
}

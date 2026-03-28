package ru.belov.ourabroad.api.usecases.services.comments;

import ru.belov.ourabroad.core.domain.Context;

public interface CommentLikeService {

    /**
     * Toggle-лайк: нет записи → +1, есть → −1.
     *
     * @return результат или {@code null} при ошибке
     */
    CommentLikeToggleResult likeComment(String userId, String commentId, Context context);
}

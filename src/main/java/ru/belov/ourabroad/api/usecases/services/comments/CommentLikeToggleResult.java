package ru.belov.ourabroad.api.usecases.services.comments;

/**
 * @param likedAfter {@code true}, если после операции лайк у пользователя есть (поставили), иначе сняли.
 */
public record CommentLikeToggleResult(boolean likedAfter) {
}

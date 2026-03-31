package ru.belov.ourabroad.core.enums;

import java.util.Optional;

/**
 * Тип контента, к которому привязан комментарий. Новые значения добавляются без изменения схемы домена.
 */
public enum CommentEntityType {

    ARTICLE,
    DISCUSSION,
    ANSWER;

    public static Optional<CommentEntityType> parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(CommentEntityType.valueOf(raw.trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}

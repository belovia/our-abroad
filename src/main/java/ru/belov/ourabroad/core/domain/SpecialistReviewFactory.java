package ru.belov.ourabroad.core.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public final class SpecialistReviewFactory {

    private SpecialistReviewFactory() {
    }

    public static SpecialistReview fromDb(
            String id,
            String bookingId,
            String authorId,
            String specialistId,
            int score,
            String text,
            LocalDateTime createdAt
    ) {
        return SpecialistReview.builder()
                .id(id)
                .bookingId(bookingId)
                .authorId(authorId)
                .specialistId(specialistId)
                .score(score)
                .text(text)
                .createdAt(createdAt)
                .build();
    }

    public static SpecialistReview create(
            String bookingId,
            String authorId,
            String specialistId,
            int score,
            String text
    ) {
        Objects.requireNonNull(bookingId, "bookingId must not be null");
        Objects.requireNonNull(authorId, "authorId must not be null");
        Objects.requireNonNull(specialistId, "specialistId must not be null");

        return SpecialistReview.builder()
                .id(UUID.randomUUID().toString())
                .bookingId(bookingId)
                .authorId(authorId)
                .specialistId(specialistId)
                .score(score)
                .text(text)
                .createdAt(LocalDateTime.now())
                .build();
    }
}

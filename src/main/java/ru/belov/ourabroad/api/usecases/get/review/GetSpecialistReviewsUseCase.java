package ru.belov.ourabroad.api.usecases.get.review;

import java.time.LocalDateTime;
import java.util.List;

public interface GetSpecialistReviewsUseCase {

    Response execute(Request request);

    record Request(String profileId) {
    }

    record ReviewItem(
            String id,
            String bookingId,
            String authorId,
            int score,
            String text,
            LocalDateTime createdAt
    ) {
    }

    record Response(
            boolean success,
            String message,
            List<ReviewItem> reviews
    ) {
    }
}

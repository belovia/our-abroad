package ru.belov.ourabroad.api.usecases.create.review;

public interface CreateSpecialistReviewUseCase {

    Response execute(Request request);

    record Request(String bookingId, int score, String text) {
    }

    record Response(
            String reviewId,
            boolean success,
            String message
    ) {
    }
}

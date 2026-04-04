package ru.belov.ourabroad.api.usecases.change.booking;

public interface CancelBookingUseCase {

    Response execute(Request request);

    record Request(String userId, String bookingId) {
    }

    record Response(boolean success, String message) {
    }
}

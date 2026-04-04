package ru.belov.ourabroad.api.usecases.change.booking;

public interface ConfirmBookingUseCase {

    Response execute(Request request);

    record Request(String specialistUserId, String bookingId) {
    }

    record Response(boolean success, String message) {
    }
}

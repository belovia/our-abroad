package ru.belov.ourabroad.api.usecases.create.booking;

import ru.belov.ourabroad.core.enums.BookingStatus;

import java.time.LocalDateTime;

public interface CreateBookingUseCase {

    Response execute(Request request);

    record Request(
            String userId,
            String specialistId,
            String serviceId,
            LocalDateTime startTime
    ) {
    }

    record Response(
            boolean success,
            String message,
            String bookingId,
            String userId,
            String specialistId,
            String serviceId,
            LocalDateTime startTime,
            BookingStatus status,
            LocalDateTime createdAt
    ) {
    }
}

package ru.belov.ourabroad.api.usecases.get.booking;

import ru.belov.ourabroad.core.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface GetSpecialistBookingsUseCase {

    Response execute(Request request);

    record Request() {
    }

    record BookingItem(
            String id,
            String userId,
            String specialistId,
            String serviceId,
            LocalDateTime startTime,
            BookingStatus status,
            LocalDateTime createdAt
    ) {
    }

    record Response(boolean success, String message, List<BookingItem> bookings) {
    }
}

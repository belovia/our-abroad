package ru.belov.ourabroad.web.dto;

import ru.belov.ourabroad.core.enums.BookingStatus;

import java.time.LocalDateTime;

public record BookingResponse(
        Boolean success,
        String message,
        String id,
        String userId,
        String specialistId,
        String serviceId,
        LocalDateTime startTime,
        BookingStatus status,
        LocalDateTime createdAt
) {
}

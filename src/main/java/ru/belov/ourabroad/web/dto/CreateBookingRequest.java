package ru.belov.ourabroad.web.dto;

import java.time.LocalDateTime;

public record CreateBookingRequest(
        String specialistId,
        String serviceId,
        LocalDateTime startTime
) {
}

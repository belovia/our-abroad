package ru.belov.ourabroad.core.domain;

import ru.belov.ourabroad.core.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public final class BookingFactory {

    private BookingFactory() {
    }

    public static Booking fromDb(
            String id,
            String userId,
            String specialistId,
            String serviceId,
            LocalDateTime startTime,
            BookingStatus status,
            LocalDateTime createdAt
    ) {
        return Booking.builder()
                .id(id)
                .userId(userId)
                .specialistId(specialistId)
                .serviceId(serviceId)
                .startTime(startTime)
                .status(status)
                .createdAt(createdAt)
                .build();
    }

    public static Booking create(
            String userId,
            String specialistId,
            String serviceId,
            LocalDateTime startTime
    ) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(specialistId, "specialistId must not be null");
        Objects.requireNonNull(serviceId, "serviceId must not be null");
        Objects.requireNonNull(startTime, "startTime must not be null");

        LocalDateTime createdAt = LocalDateTime.now();
        return Booking.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .specialistId(specialistId)
                .serviceId(serviceId)
                .startTime(startTime)
                .status(BookingStatus.PENDING)
                .createdAt(createdAt)
                .build();
    }
}

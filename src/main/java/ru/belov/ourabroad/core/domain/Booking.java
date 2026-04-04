package ru.belov.ourabroad.core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.belov.ourabroad.core.enums.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class Booking {

    private final String id;
    private final String userId;
    private final String specialistId;
    private final String serviceId;

    private LocalDateTime startTime;
    private BookingStatus status;

    private final LocalDateTime createdAt;
}

package ru.belov.ourabroad.core.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SpecialistReview {

    private final String id;
    private final String bookingId;
    private final String authorId;
    private final String specialistId;
    private final int score;
    private final String text;
    private final LocalDateTime createdAt;
}

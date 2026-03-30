package ru.belov.ourabroad.web.dto.qa.read;

import java.time.LocalDateTime;

public record AnswerResponse(
        String id,
        String content,
        int votes,
        boolean accepted,
        LocalDateTime createdAt,
        String specialistProfileId
) {
}


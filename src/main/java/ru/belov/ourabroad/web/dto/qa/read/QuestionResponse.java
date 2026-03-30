package ru.belov.ourabroad.web.dto.qa.read;

import java.time.LocalDateTime;
import java.util.Set;

public record QuestionResponse(
        String id,
        String authorId,
        String title,
        String content,
        Set<String> tags,
        int votes,
        int answersCount,
        LocalDateTime createdAt
) {
}


package ru.belov.ourabroad.web.dto.qa;

import java.util.Set;

public record CreateQuestionBody(String authorId, String title, String content, Set<String> tags) {
}

package ru.belov.ourabroad.web.dto.qa;

import java.util.Set;

public record CreateQuestionBody(String title, String content, Set<String> tags) {
}

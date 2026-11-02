package ru.belov.ourabroad.api.usecases.qa;

import java.util.Set;

public interface CreateQuestionUseCase {

    Response execute(Request request);

    record Request(String title, String content, Set<String> tags) {
    }

    record Response(String questionId, boolean success, String errorMessage) {
    }
}

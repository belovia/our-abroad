package ru.belov.ourabroad.api.usecases.qa;

public interface AnswerQuestionUseCase {

    Response execute(Request request);

    record Request(String questionId, String authorId, String content) {
    }

    record Response(String answerId, String questionId, boolean success, String errorMessage) {
    }
}

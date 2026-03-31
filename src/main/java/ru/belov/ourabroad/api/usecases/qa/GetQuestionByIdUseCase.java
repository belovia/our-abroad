package ru.belov.ourabroad.api.usecases.qa;

import ru.belov.ourabroad.web.dto.qa.read.QuestionResponse;

public interface GetQuestionByIdUseCase {

    Response execute(Request request);

    record Request(String questionId) {
    }

    record Response(QuestionResponse question, boolean success, String errorMessage) {
    }
}


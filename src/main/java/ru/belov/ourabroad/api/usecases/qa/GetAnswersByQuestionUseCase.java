package ru.belov.ourabroad.api.usecases.qa;

import ru.belov.ourabroad.web.dto.qa.read.AnswerResponse;

import java.util.List;

public interface GetAnswersByQuestionUseCase {

    Response execute(Request request);

    record Request(String questionId) {
    }

    record Response(List<AnswerResponse> answers, boolean success, String errorMessage) {
    }
}


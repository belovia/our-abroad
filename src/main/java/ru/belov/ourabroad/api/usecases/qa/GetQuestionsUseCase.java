package ru.belov.ourabroad.api.usecases.qa;

import ru.belov.ourabroad.web.dto.qa.read.QuestionResponse;

import java.util.List;

public interface GetQuestionsUseCase {

    Response execute(Request request);

    enum SortMode {
        NEWEST,
        VOTES
    }

    record Request(Integer page, Integer size, SortMode sort, String tag) {
    }

    record Response(List<QuestionResponse> questions, boolean success, String errorMessage) {
    }
}


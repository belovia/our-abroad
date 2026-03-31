package ru.belov.ourabroad.api.usecases.services.qa;

public record AcceptAnswerResult(
        String answerAuthorId,
        String questionAuthorId,
        boolean changed
) {
}


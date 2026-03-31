package ru.belov.ourabroad.api.usecases.change.reputation;

public interface AddReputationPointsUseCase {

    Response execute(Request request);

    record Request(String userId, int points) {
    }

    record Response(String userId, boolean success, String errorMessage) {
    }
}

package ru.belov.ourabroad.api.usecases.create.reputation;

public interface CreateReputationUseCase {

    Response execute(Request request);

    record Request(String userId) {
    }

    record Response(String userId, boolean success, String errorMessage) {
    }
}

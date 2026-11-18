package ru.belov.ourabroad.api.usecases.change.user;

public interface ActivateUserUseCase {
    Response execute(Request request);

    record Request(String userId) {
    }

    record Response(
            String userId,
            boolean success,
            String message
    ) {
    }
}

package ru.belov.ourabroad.api.usecases.change.user;

public interface ChangeUserEmailUseCase {
    Response execute(Request request);

    record Request(String newEmail) {
    }

    record Response(
            String userId,
            boolean success,
            String errorMessage
    ) {
    }
}

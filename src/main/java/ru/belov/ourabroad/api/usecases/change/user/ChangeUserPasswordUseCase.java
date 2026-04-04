package ru.belov.ourabroad.api.usecases.change.user;

public interface ChangeUserPasswordUseCase {
    Response execute(Request request);

    record Request(String oldPassword, String newPassword) {
    }

    record Response(
            String userId,
            boolean success,
            String errorMessage
    ) {
    }
}

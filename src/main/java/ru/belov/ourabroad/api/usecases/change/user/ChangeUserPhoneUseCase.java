package ru.belov.ourabroad.api.usecases.change.user;

public interface ChangeUserPhoneUseCase {
    Response execute(Request request);

    record Request(String newPhone) {
    }

    record Response(
            String userId,
            boolean success,
            String message
    ) {
    }
}

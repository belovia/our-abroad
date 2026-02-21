package ru.belov.ourabroad.api.usecases.create.user;

public interface CreateUserUseCase {

    Response execute(Request request);

    record Request(
            String email,
            String phone,
            String password,
            String telegramUsername,
            String whatsAppNumber,
            String activity
    ) {
    }

    record Response(
            String userId,
            boolean success,
            String errorMessage
    ) {}
}

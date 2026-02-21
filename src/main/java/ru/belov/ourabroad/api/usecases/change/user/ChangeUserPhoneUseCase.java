package ru.belov.ourabroad.api.usecases.change.user;

public interface ChangeUserPhoneUseCase {
    Response execute(Request request);

    public record Request(
            String userId,
            String newPhone
    ) {}

    public record Response(
            String userId,
            boolean success,
            String message
    ) {}
}

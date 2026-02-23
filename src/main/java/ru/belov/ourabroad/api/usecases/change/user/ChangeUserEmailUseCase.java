package ru.belov.ourabroad.api.usecases.change.user;

public interface ChangeUserEmailUseCase {
    Response execute(Request request);

    public record Request(
            String userId,
            String newEmail
    ) {}

    public record Response(
            String userId,
            boolean success,
            String errorMessage
    ) {}
}

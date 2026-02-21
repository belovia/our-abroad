package ru.belov.ourabroad.api.usecases.change.user;

public interface ChangeUserPasswordUseCase {
    Response execute(Request request);

    public record Request(
            String userId,
            String oldPassword,
            String newPassword
    ) {}

    public record Response(
            String userId,
            boolean success,
            String errorMessage
    ) {}

}

package ru.belov.ourabroad.api.usecases.change.user;

public interface ChangeUserEmailUseCase {
    void changeEmail(String userId, String newEmail);
}

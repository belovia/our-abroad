package ru.belov.ourabroad.api.usecases.change.user;

public interface ChangeUserPasswordUseCase {
    void changePassword(String userId, String oldPassword, String password);
}

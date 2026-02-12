package ru.belov.ourabroad.api.usecases.change;

public interface ChangeUserPasswordUseCase {
    void changePassword(String userId, String oldPassword, String password);
}

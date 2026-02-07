package ru.belov.ourabroad.api.usecases;

public interface UserUpdateUsecase {

    void updateEmail(String userId, String email);
    void updatePassword(String userId, String password);
    void updatePhone(String userId, String phone);

}

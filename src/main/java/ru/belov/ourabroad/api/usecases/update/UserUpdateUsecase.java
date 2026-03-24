package ru.belov.ourabroad.api.usecases.update;

import ru.belov.ourabroad.web.dto.update.UpdateUserRequest;

public interface UserUpdateUsecase {

    public void updateUser(String userId, UpdateUserRequest request);

}

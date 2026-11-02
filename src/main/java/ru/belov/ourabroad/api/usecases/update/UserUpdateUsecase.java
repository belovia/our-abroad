package ru.belov.ourabroad.api.usecases.update;

import ru.belov.ourabroad.web.dto.update.UpdateUserRequest;

public interface UserUpdateUsecase {

    void updateUser(UpdateUserRequest request);
}

package ru.belov.ourabroad.api.usecases.get.user;

import ru.belov.ourabroad.core.domain.User;

public interface GetUserByIdUsecase {

    User getUserById(String userId);
}

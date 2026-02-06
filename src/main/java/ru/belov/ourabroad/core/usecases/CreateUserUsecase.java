package ru.belov.ourabroad.core.usecases;

import ru.belov.ourabroad.web.dto.CreateUserRequest;

public interface CreateUserUsecase {

    String create(CreateUserRequest command);
}

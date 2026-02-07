package ru.belov.ourabroad.api.usecases;

import ru.belov.ourabroad.web.dto.create.CreateUserRequest;

public interface CreateUserUsecase {

    String create(CreateUserRequest command);
}

package ru.belov.ourabroad.api.usecases.create.user;

import ru.belov.ourabroad.web.dto.create.CreateUserRequest;

public interface CreateUserUseCase {

    String create(CreateUserRequest command);
}

package ru.belov.ourabroad.api.usecases;

import ru.belov.ourabroad.core.domain.User;

import java.util.Optional;

public interface GetUserUsecase {

    User getUserById(String userId);
    User getUserByEmail(String email);
}

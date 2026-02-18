package ru.belov.ourabroad.api.usecases.get.user;

import ru.belov.ourabroad.core.domain.User;

public interface GetUserByEmailUseCase {
    User getUserByEmail(String email);

}

package ru.belov.ourabroad.api.usecases.get;

import ru.belov.ourabroad.core.domain.User;

public interface GetUserByEmailUseCase {
    User getUserByEmail(String email);

}

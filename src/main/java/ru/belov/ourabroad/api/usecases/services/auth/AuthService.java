package ru.belov.ourabroad.api.usecases.services.auth;

import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;

public interface AuthService {

    User authenticateByEmailAndPassword(String email, String rawPassword, Context context);
}

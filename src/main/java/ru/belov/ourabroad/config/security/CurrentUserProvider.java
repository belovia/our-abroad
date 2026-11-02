package ru.belov.ourabroad.config.security;

import java.util.Optional;

public interface CurrentUserProvider {

    /**
     * Идентификатор текущего пользователя из JWT (обязателен для защищённых эндпоинтов).
     */
    String requiredUserId();

    Optional<String> currentUserId();

    JwtUserPrincipal requiredPrincipal();
}

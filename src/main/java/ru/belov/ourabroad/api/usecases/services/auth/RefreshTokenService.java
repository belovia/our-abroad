package ru.belov.ourabroad.api.usecases.services.auth;

import ru.belov.ourabroad.core.domain.Context;

import java.util.Optional;

public interface RefreshTokenService {

    /**
     * Создаёт refresh-токен, сохраняет хэш в БД. Возвращает сырой токен для клиента.
     */
    String issueRefreshToken(String userId, String deviceInfo, Context context);

    /**
     * Ротация: удаляет старую запись и выдаёт новый сырой токен.
     */
    Optional<RefreshRotationResult> rotateRefreshToken(String rawRefreshToken, Context context);

    void revokeRefreshToken(String rawRefreshToken, Context context);

    void revokeAllForUser(String userId, Context context);
}

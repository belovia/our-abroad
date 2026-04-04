package ru.belov.ourabroad.poi.storage;

import ru.belov.ourabroad.core.domain.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

    void save(RefreshToken token);

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    void deleteById(String id);

    void deleteByTokenHash(String tokenHash);

    void deleteAllByUserId(String userId);
}

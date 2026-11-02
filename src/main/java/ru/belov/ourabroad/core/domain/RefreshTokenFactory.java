package ru.belov.ourabroad.core.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public final class RefreshTokenFactory {

    private RefreshTokenFactory() {
    }

    public static RefreshToken fromDb(
            String id,
            String userId,
            String tokenHash,
            LocalDateTime expiresAt,
            LocalDateTime createdAt,
            String deviceInfo
    ) {
        return RefreshToken.builder()
                .id(id)
                .userId(userId)
                .tokenHash(tokenHash)
                .expiresAt(expiresAt)
                .createdAt(createdAt)
                .deviceInfo(deviceInfo)
                .build();
    }

    public static RefreshToken newRecord(String userId, String tokenHash, LocalDateTime expiresAt, String deviceInfo) {
        return RefreshToken.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .tokenHash(tokenHash)
                .expiresAt(expiresAt)
                .createdAt(LocalDateTime.now())
                .deviceInfo(deviceInfo)
                .build();
    }
}

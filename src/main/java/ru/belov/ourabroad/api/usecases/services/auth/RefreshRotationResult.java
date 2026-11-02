package ru.belov.ourabroad.api.usecases.services.auth;

public record RefreshRotationResult(String userId, String newRawRefreshToken) {
}

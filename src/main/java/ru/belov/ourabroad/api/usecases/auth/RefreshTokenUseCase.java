package ru.belov.ourabroad.api.usecases.auth;

public interface RefreshTokenUseCase {

    AuthenticationUseCase.AuthResponse refresh(RefreshRequest request);

    LogoutResponse logout(LogoutRequest request);

    record RefreshRequest(String refreshToken) {
    }

    record LogoutRequest(String refreshToken) {
    }

    record LogoutResponse(boolean success, String message) {
    }
}

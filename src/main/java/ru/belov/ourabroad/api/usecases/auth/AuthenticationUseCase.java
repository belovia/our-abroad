package ru.belov.ourabroad.api.usecases.auth;

public interface AuthenticationUseCase {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    record RegisterRequest(
            String email,
            String phone,
            String password,
            String telegramUsername,
            String whatsAppNumber,
            String activity
    ) {
    }

    record LoginRequest(String email, String password, String deviceInfo) {
    }

    record AuthResponse(
            boolean success,
            String message,
            String userId,
            String accessToken,
            String refreshToken,
            long expiresInSeconds,
            String tokenType
    ) {
        public static AuthResponse error(String message) {
            return new AuthResponse(false, message, null, null, null, 0, null);
        }
    }
}

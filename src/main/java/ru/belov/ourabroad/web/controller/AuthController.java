package ru.belov.ourabroad.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.belov.ourabroad.api.usecases.auth.AuthenticationUseCase;
import ru.belov.ourabroad.api.usecases.auth.RefreshTokenUseCase;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationUseCase authenticationUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationUseCase.AuthResponse> register(
            @RequestBody AuthenticationUseCase.RegisterRequest body
    ) {
        return ResponseEntity.ok(authenticationUseCase.register(body));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationUseCase.AuthResponse> login(
            @RequestBody AuthenticationUseCase.LoginRequest body
    ) {
        return ResponseEntity.ok(authenticationUseCase.login(body));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationUseCase.AuthResponse> refresh(
            @RequestBody RefreshTokenUseCase.RefreshRequest body
    ) {
        return ResponseEntity.ok(refreshTokenUseCase.refresh(body));
    }

    @PostMapping("/logout")
    public ResponseEntity<RefreshTokenUseCase.LogoutResponse> logout(
            @RequestBody RefreshTokenUseCase.LogoutRequest body
    ) {
        return ResponseEntity.ok(refreshTokenUseCase.logout(body));
    }
}

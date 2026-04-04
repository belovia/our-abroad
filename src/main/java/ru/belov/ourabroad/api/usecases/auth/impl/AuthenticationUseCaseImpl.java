package ru.belov.ourabroad.api.usecases.auth.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.auth.AuthenticationUseCase;
import ru.belov.ourabroad.api.usecases.create.user.CreateUserUseCase;
import ru.belov.ourabroad.api.usecases.services.auth.AuthService;
import ru.belov.ourabroad.api.usecases.services.auth.RefreshTokenService;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.config.security.JwtService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.core.domain.UserFactory;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.UserValidator;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationUseCaseImpl implements AuthenticationUseCase {

    private final UserValidator userValidator;
    private final UserService userService;
    private final AuthService authService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        Context context = new Context();
        String userId = UUID.randomUUID().toString();
        log.info("[userId: {}] Register via /api/auth", userId);

        var createRequest = new CreateUserUseCase.Request(
                request.email(),
                request.phone(),
                request.password(),
                request.telegramUsername(),
                request.whatsAppNumber(),
                request.activity()
        );
        userValidator.validateCreateUserRequest(createRequest, context);
        if (!context.isSuccess()) {
            return AuthResponse.error(context.getErrorMessage());
        }

        if (userService.existsByEmail(userId, request.email(), context)) {
            context.setError(ErrorCode.EMAIL_ALREADY_EXISTS);
            return AuthResponse.error(ErrorCode.EMAIL_ALREADY_EXISTS.getMessage());
        }

        User user = UserFactory.newUser(
                userId,
                request.email(),
                request.phone(),
                passwordEncoder.encode(request.password()),
                request.telegramUsername(),
                request.whatsAppNumber(),
                request.activity()
        );
        userService.save(user, context);
        if (!context.isSuccess()) {
            return AuthResponse.error(context.getErrorMessage());
        }

        return issueTokens(user, request.activity(), context);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Context context = new Context();
        log.info("Login attempt for email: {}", request.email() != null ? request.email() : "");

        User user = authService.authenticateByEmailAndPassword(
                request.email(),
                request.password(),
                context
        );
        if (!context.isSuccess() || user == null) {
            return AuthResponse.error(context.getErrorMessage() != null
                    ? context.getErrorMessage()
                    : ErrorCode.INVALID_CREDENTIALS.getMessage());
        }

        user.updateLastLogin();
        userService.update(user, context);
        if (!context.isSuccess()) {
            return AuthResponse.error(context.getErrorMessage());
        }

        return issueTokens(user, request.deviceInfo(), context);
    }

    private AuthResponse issueTokens(User user, String deviceInfo, Context context) {
        Context tokenContext = new Context();
        String access = jwtService.createAccessToken(user);
        String refresh = refreshTokenService.issueRefreshToken(user.getId(), deviceInfo, tokenContext);
        if (!tokenContext.isSuccess() || refresh == null) {
            return AuthResponse.error(tokenContext.getErrorMessage() != null
                    ? tokenContext.getErrorMessage()
                    : ErrorCode.DB_ERROR.getMessage());
        }
        return new AuthResponse(
                true,
                ErrorCode.SUCCESS.getMessage(),
                user.getId(),
                access,
                refresh,
                jwtService.accessTokenTtlSeconds(),
                "Bearer"
        );
    }
}

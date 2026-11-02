package ru.belov.ourabroad.api.usecases.auth.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.auth.AuthenticationUseCase;
import ru.belov.ourabroad.api.usecases.auth.RefreshTokenUseCase;
import ru.belov.ourabroad.api.usecases.services.auth.RefreshTokenService;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.config.security.JwtService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.web.validators.ErrorCode;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenUseCaseImpl implements RefreshTokenUseCase {

    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthenticationUseCase.AuthResponse refresh(RefreshRequest request) {
        Context context = new Context();
        var rotated = refreshTokenService.rotateRefreshToken(request.refreshToken(), context);
        if (rotated.isEmpty()) {
            return AuthenticationUseCase.AuthResponse.error(
                    context.getErrorMessage() != null
                            ? context.getErrorMessage()
                            : ErrorCode.REFRESH_TOKEN_INVALID.getMessage()
            );
        }

        Context loadCtx = new Context();
        User user = userService.findById(rotated.get().userId(), loadCtx);
        if (!loadCtx.isSuccess() || user == null) {
            return AuthenticationUseCase.AuthResponse.error(
                    loadCtx.getErrorMessage() != null
                            ? loadCtx.getErrorMessage()
                            : ErrorCode.USER_NOT_FOUND.getMessage()
            );
        }

        String access = jwtService.createAccessToken(user);
        return new AuthenticationUseCase.AuthResponse(
                true,
                ErrorCode.SUCCESS.getMessage(),
                user.getId(),
                access,
                rotated.get().newRawRefreshToken(),
                jwtService.accessTokenTtlSeconds(),
                "Bearer"
        );
    }

    @Override
    @Transactional
    public LogoutResponse logout(LogoutRequest request) {
        Context context = new Context();
        refreshTokenService.revokeRefreshToken(request.refreshToken(), context);
        if (!context.isSuccess()) {
            return new LogoutResponse(false, context.getErrorMessage());
        }
        return new LogoutResponse(true, ErrorCode.SUCCESS.getMessage());
    }
}

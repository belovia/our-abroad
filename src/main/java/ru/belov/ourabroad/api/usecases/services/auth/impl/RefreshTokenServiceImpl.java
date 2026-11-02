package ru.belov.ourabroad.api.usecases.services.auth.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.services.auth.RefreshRotationResult;
import ru.belov.ourabroad.api.usecases.services.auth.RefreshTokenService;
import ru.belov.ourabroad.config.security.JwtProperties;
import ru.belov.ourabroad.config.security.TokenHasher;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.RefreshToken;
import ru.belov.ourabroad.core.domain.RefreshTokenFactory;
import ru.belov.ourabroad.poi.storage.RefreshTokenRepository;
import ru.belov.ourabroad.web.validators.ErrorCode;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    @Override
    @Transactional
    public String issueRefreshToken(String userId, String deviceInfo, Context context) {
        if (!context.isSuccess()) {
            return null;
        }
        String raw = generateRawToken();
        String hash = TokenHasher.sha256Hex(raw);
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(jwtProperties.getRefreshTokenDays());
        RefreshToken entity = RefreshTokenFactory.newRecord(userId, hash, expiresAt, deviceInfo);
        refreshTokenRepository.save(entity);
        return raw;
    }

    @Override
    @Transactional
    public Optional<RefreshRotationResult> rotateRefreshToken(String rawRefreshToken, Context context) {
        if (!context.isSuccess()) {
            return Optional.empty();
        }
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            context.setError(ErrorCode.REFRESH_TOKEN_INVALID);
            return Optional.empty();
        }
        String hash = TokenHasher.sha256Hex(rawRefreshToken);
        Optional<RefreshToken> existingOpt = refreshTokenRepository.findByTokenHash(hash);
        if (existingOpt.isEmpty()) {
            log.warn("Refresh token hash not found (possible reuse or forgery)");
            context.setError(ErrorCode.REFRESH_TOKEN_REUSED);
            return Optional.empty();
        }
        RefreshToken existing = existingOpt.get();
        if (existing.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.deleteById(existing.getId());
            context.setError(ErrorCode.TOKEN_EXPIRED);
            return Optional.empty();
        }
        String userId = existing.getUserId();
        String deviceInfo = existing.getDeviceInfo();
        refreshTokenRepository.deleteById(existing.getId());
        Context issueCtx = new Context();
        String newRaw = issueRefreshToken(userId, deviceInfo, issueCtx);
        if (!issueCtx.isSuccess() || newRaw == null) {
            if (issueCtx.getErrorCode() != null) {
                context.setError(issueCtx.getErrorCode());
            } else {
                context.setError(ErrorCode.DB_ERROR);
            }
            return Optional.empty();
        }
        return Optional.of(new RefreshRotationResult(userId, newRaw));
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String rawRefreshToken, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            context.setError(ErrorCode.REFRESH_TOKEN_INVALID);
            return;
        }
        String hash = TokenHasher.sha256Hex(rawRefreshToken);
        refreshTokenRepository.deleteByTokenHash(hash);
    }

    @Override
    @Transactional
    public void revokeAllForUser(String userId, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        refreshTokenRepository.deleteAllByUserId(userId);
    }

    private static String generateRawToken() {
        byte[] bytes = new byte[48];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}

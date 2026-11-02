package ru.belov.ourabroad.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.core.domain.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtService {

    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_EMAIL = "email";
    public static final String CLAIM_ROLES = "roles";

    private final JwtProperties properties;

    public String createAccessToken(User user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(properties.getAccessTokenMinutes() * 60);
        return Jwts.builder()
                .subject(user.getId())
                .claim(CLAIM_USER_ID, user.getId())
                .claim(CLAIM_EMAIL, user.getEmail())
                .claim(CLAIM_ROLES, user.getRoles())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(signingKey())
                .compact();
    }

    public Claims parseClaims(String token) throws ExpiredJwtException, JwtException {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public JwtUserPrincipal toPrincipal(Claims claims) {
        String userId = claims.get(CLAIM_USER_ID, String.class);
        if (userId == null) {
            userId = claims.getSubject();
        }
        String email = claims.get(CLAIM_EMAIL, String.class);
        String roles = claims.get(CLAIM_ROLES, String.class);
        if (roles == null || roles.isBlank()) {
            roles = ru.belov.ourabroad.core.security.AppRoles.DEFAULT;
        }
        return new JwtUserPrincipal(userId, email != null ? email : "", roles);
    }

    public long accessTokenTtlSeconds() {
        return properties.getAccessTokenMinutes() * 60;
    }

    private SecretKey signingKey() {
        byte[] keyBytes = properties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

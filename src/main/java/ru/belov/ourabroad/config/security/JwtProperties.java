package ru.belov.ourabroad.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    /**
     * HMAC key (UTF-8); must be long enough for HS-256.
     */
    private String secret = "dev-only-change-me-use-at-least-256-bits-secret-key-for-hs256!!";

    private long accessTokenMinutes = 15;

    private long refreshTokenDays = 30;
}

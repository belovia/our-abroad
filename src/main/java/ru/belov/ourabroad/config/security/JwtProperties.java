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
    private String secret = "dev";

    private long accessTokenMinutes = 10000;

    private long refreshTokenDays = 300;
}

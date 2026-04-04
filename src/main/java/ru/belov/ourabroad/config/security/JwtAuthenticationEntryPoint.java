package ru.belov.ourabroad.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.web.validators.ErrorCode;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        Object jwtEx = request.getAttribute("jwtException");
        ErrorCode code = ErrorCode.UNAUTHORIZED;
        if (jwtEx instanceof io.jsonwebtoken.ExpiredJwtException) {
            code = ErrorCode.TOKEN_EXPIRED;
        } else if (jwtEx instanceof io.jsonwebtoken.JwtException) {
            code = ErrorCode.TOKEN_INVALID;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(), Map.of(
                "success", false,
                "code", code.getCode(),
                "message", code.getMessage()
        ));
    }
}

package ru.belov.ourabroad.api.usecases.services.auth.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.belov.ourabroad.api.usecases.services.auth.AuthService;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.core.enums.UserStatus;
import ru.belov.ourabroad.web.validators.ErrorCode;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User authenticateByEmailAndPassword(String email, String rawPassword, Context context) {
        if (!context.isSuccess()) {
            return null;
        }
        if (!StringUtils.hasText(email) || !StringUtils.hasText(rawPassword)) {
            context.setError(ErrorCode.INVALID_CREDENTIALS);
            return null;
        }
        var userOpt = userService.findByEmailRaw(email);
        if (userOpt.isEmpty()) {
            context.setError(ErrorCode.INVALID_CREDENTIALS);
            return null;
        }
        User user = userOpt.get();
        if (user.getStatus() != UserStatus.ACTIVE) {
            context.setError(ErrorCode.INVALID_CREDENTIALS);
            return null;
        }
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            context.setError(ErrorCode.INVALID_CREDENTIALS);
            return null;
        }
        return user;
    }
}

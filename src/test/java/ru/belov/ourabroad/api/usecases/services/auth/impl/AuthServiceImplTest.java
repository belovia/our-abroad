package ru.belov.ourabroad.api.usecases.services.auth.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.core.enums.UserStatus;
import ru.belov.ourabroad.core.security.AppRoles;
import ru.belov.ourabroad.web.validators.ErrorCode;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    private static final String EMAIL = "user@example.com";
    private static final String RAW_PASSWORD = "Abcdef1!";
    private static final String HASH = "hash";

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void stubs() {
        lenient().when(passwordEncoder.matches(RAW_PASSWORD, HASH)).thenReturn(true);
    }

    @Test
    void WHEN_activeUserWithValidPassword_THEN_returnsUser() {
        User user = userWithStatus(UserStatus.ACTIVE);
        when(userService.findByEmailRaw(EMAIL)).thenReturn(Optional.of(user));
        Context context = new Context();

        User result = authService.authenticateByEmailAndPassword(EMAIL, RAW_PASSWORD, context);

        assertThat(result).isSameAs(user);
        assertThat(context.isSuccess()).isTrue();
    }

    @Test
    void WHEN_blockedUser_THEN_userBlockedError() {
        when(userService.findByEmailRaw(EMAIL)).thenReturn(Optional.of(userWithStatus(UserStatus.BLOCKED)));
        Context context = new Context();

        User result = authService.authenticateByEmailAndPassword(EMAIL, RAW_PASSWORD, context);

        assertThat(result).isNull();
        assertThat(context.isSuccess()).isFalse();
        assertThat(context.getErrorMessage()).isEqualTo(ErrorCode.USER_BLOCKED.getMessage());
    }

    @Test
    void WHEN_deletedUser_THEN_invalidCredentials() {
        when(userService.findByEmailRaw(EMAIL)).thenReturn(Optional.of(userWithStatus(UserStatus.DELETED)));
        Context context = new Context();

        User result = authService.authenticateByEmailAndPassword(EMAIL, RAW_PASSWORD, context);

        assertThat(result).isNull();
        assertThat(context.getErrorMessage()).isEqualTo(ErrorCode.INVALID_CREDENTIALS.getMessage());
    }

    @Test
    void WHEN_unknownEmail_THEN_invalidCredentials() {
        when(userService.findByEmailRaw(anyString())).thenReturn(Optional.empty());
        Context context = new Context();

        User result = authService.authenticateByEmailAndPassword(EMAIL, RAW_PASSWORD, context);

        assertThat(result).isNull();
        assertThat(context.getErrorMessage()).isEqualTo(ErrorCode.INVALID_CREDENTIALS.getMessage());
    }

    private static User userWithStatus(UserStatus status) {
        return User.create(
                "user-1",
                EMAIL,
                null,
                HASH,
                status,
                null,
                null,
                null,
                AppRoles.DEFAULT,
                LocalDateTime.now(),
                null
        );
    }
}

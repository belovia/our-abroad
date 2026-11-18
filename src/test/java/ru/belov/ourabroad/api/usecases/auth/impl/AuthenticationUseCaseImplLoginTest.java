package ru.belov.ourabroad.api.usecases.auth.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.auth.AuthenticationUseCase;
import ru.belov.ourabroad.api.usecases.services.auth.AuthService;
import ru.belov.ourabroad.api.usecases.services.auth.RefreshTokenService;
import ru.belov.ourabroad.api.usecases.services.profile.ProfileService;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.config.security.JwtService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.UserValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AuthenticationUseCaseImpl.class, UserValidator.class})
class AuthenticationUseCaseImplLoginTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private ProfileService profileService;

    @Autowired
    private AuthenticationUseCase authenticationUseCase;

    @Test
    void WHEN_authServiceReportsBlocked_THEN_noTokensAndNoLastLoginUpdate() {
        when(authService.authenticateByEmailAndPassword(eq("user@example.com"), eq("Abcdef1!"), any(Context.class)))
                .thenAnswer(invocation -> {
                    Context context = invocation.getArgument(2);
                    context.setError(ErrorCode.USER_BLOCKED);
                    return null;
                });

        var response = authenticationUseCase.login(
                new AuthenticationUseCase.LoginRequest("user@example.com", "Abcdef1!", null)
        );

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.USER_BLOCKED.getMessage());
        assertThat(response.accessToken()).isNull();
        assertThat(response.refreshToken()).isNull();
        verify(userService, never()).update(any(User.class), any(Context.class));
        verify(jwtService, never()).createAccessToken(any(User.class));
        verify(refreshTokenService, never()).issueRefreshToken(anyString(), any(), any(Context.class));
    }
}

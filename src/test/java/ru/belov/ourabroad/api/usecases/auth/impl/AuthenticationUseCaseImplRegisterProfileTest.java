package ru.belov.ourabroad.api.usecases.auth.impl;

import org.junit.jupiter.api.BeforeEach;
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
import ru.belov.ourabroad.web.validators.UserValidator;

import ru.belov.ourabroad.web.validators.ErrorCode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AuthenticationUseCaseImpl.class, UserValidator.class})
class AuthenticationUseCaseImplRegisterProfileTest {

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

    @BeforeEach
    void stubs() {
        when(passwordEncoder.encode(anyString())).thenReturn("hash");
        when(userService.existsByEmail(anyString(), anyString(), any(Context.class))).thenReturn(false);
        when(jwtService.createAccessToken(any(User.class))).thenReturn("access");
        when(jwtService.accessTokenTtlSeconds()).thenReturn(60L);
        when(refreshTokenService.issueRefreshToken(anyString(), any(), any(Context.class))).thenReturn("refresh");
    }

    @Test
    void WHEN_registerSucceeds_THEN_savesEmptyProfile() {
        var response = authenticationUseCase.register(new AuthenticationUseCase.RegisterRequest(
                "user@example.com",
                null,
                "Abcdef1!",
                null,
                null,
                null
        ));

        org.assertj.core.api.Assertions.assertThat(response.success()).isTrue();
        verify(profileService).saveEmpty(anyString(), any(Context.class));
    }

    @Test
    void WHEN_registerEmailInvalid_THEN_profileIsNotSaved() {
        authenticationUseCase.register(new AuthenticationUseCase.RegisterRequest(
                "not-an-email",
                null,
                "password",
                null,
                null,
                null
        ));

        verify(profileService, never()).saveEmpty(anyString(), any(Context.class));
        verify(userService, never()).save(any(User.class), any(Context.class));
    }

    @Test
    void WHEN_registerPhoneAlreadyExists_THEN_returnPhoneAlreadyExistsError() {
        when(userService.existsByPhone(anyString(), eq("+79123456789"), any(Context.class))).thenReturn(true);

        var response = authenticationUseCase.register(new AuthenticationUseCase.RegisterRequest(
                "user@example.com",
                "+79123456789",
                "Abcdef1!",
                null,
                null,
                null
        ));

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.PHONE_ALREADY_EXISTS.getMessage());
        verify(userService, never()).save(any(User.class), any(Context.class));
        verify(profileService, never()).saveEmpty(anyString(), any(Context.class));
    }

    @Test
    void WHEN_registerWithoutPhone_THEN_success() {
        var response = authenticationUseCase.register(new AuthenticationUseCase.RegisterRequest(
                "user@example.com",
                null,
                "Abcdef1!",
                null,
                null,
                null
        ));

        assertThat(response.success()).isTrue();
        verify(userService, never()).existsByPhone(anyString(), any(), any(Context.class));
    }
}

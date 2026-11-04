package ru.belov.ourabroad.api.usecases.change.user.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.change.user.ActivateUserUseCase;
import ru.belov.ourabroad.api.usecases.services.auth.RefreshTokenService;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.core.enums.UserStatus;
import ru.belov.ourabroad.core.security.AppRoles;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.UserValidator;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ActivateUserUseCaseImpl.class, UserValidator.class})
class ActivateUserUseCaseImplTest {

    private static final String USER_ID = "user-1";

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @Autowired
    private ActivateUserUseCase useCase;

    @Test
    void WHEN_blockedUser_THEN_activatedWithoutRevokingRefresh() {
        User user = userWithStatus(UserStatus.BLOCKED);
        when(userService.findById(eq(USER_ID), any(Context.class))).thenReturn(user);

        var response = useCase.execute(new ActivateUserUseCase.Request(USER_ID));

        assertThat(response.success()).isTrue();
        assertThat(response.userId()).isEqualTo(USER_ID);
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        verify(userService).update(eq(user), any(Context.class));
        verify(refreshTokenService, never()).revokeAllForUser(anyString(), any(Context.class));
    }

    @Test
    void WHEN_alreadyActive_THEN_successAndStaysActive() {
        User user = userWithStatus(UserStatus.ACTIVE);
        when(userService.findById(eq(USER_ID), any(Context.class))).thenReturn(user);

        var response = useCase.execute(new ActivateUserUseCase.Request(USER_ID));

        assertThat(response.success()).isTrue();
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        verify(refreshTokenService, never()).revokeAllForUser(anyString(), any(Context.class));
    }

    @Test
    void WHEN_unknownUser_THEN_userNotFound() {
        when(userService.findById(eq(USER_ID), any(Context.class))).thenAnswer(invocation -> {
            Context context = invocation.getArgument(1);
            context.setError(ErrorCode.USER_NOT_FOUND);
            return null;
        });

        var response = useCase.execute(new ActivateUserUseCase.Request(USER_ID));

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());
        verify(userService, never()).update(any(User.class), any(Context.class));
    }

    private static User userWithStatus(UserStatus status) {
        return User.create(
                USER_ID, "user@example.com", null, "hash", status,
                null, null, null, AppRoles.DEFAULT, LocalDateTime.now(), null
        );
    }
}

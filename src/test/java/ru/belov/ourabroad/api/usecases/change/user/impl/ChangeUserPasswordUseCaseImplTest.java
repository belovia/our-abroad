package ru.belov.ourabroad.api.usecases.change.user.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.change.user.ChangeUserPasswordUseCase;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.config.security.CurrentUserProvider;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.core.enums.UserStatus;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.UserValidator;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                ChangeUserPasswordUseCaseImpl.class,
                UserValidator.class
        }
)
class ChangeUserPasswordUseCaseImplTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private CurrentUserProvider currentUserProvider;

    @Autowired
    private ChangeUserPasswordUseCaseImpl usecase;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private static final String USER_ID = "user-123";
    private static final String OLD_PASSWORD = "OldPassword1";
    private static final String NEW_PASSWORD = "NewPassword1";

    @BeforeEach
    void stubCurrentUserAndEncoder() {
        when(currentUserProvider.requiredUserId()).thenReturn(USER_ID);
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn("ENCODED_NEW");
        when(passwordEncoder.matches(eq(OLD_PASSWORD), eq(OLD_PASSWORD))).thenReturn(true);
    }

    @Test
    void contextCreated() {
        assertNotNull(usecase);
        assertNotNull(userService);
    }

    @Test
    void WHEN_execute_validRequest_THEN_updatePasswordSuccessfully() {
        // Arrange
        ChangeUserPasswordUseCase.Request request = createValidRequest();
        User existingUser = createUser(OLD_PASSWORD);

        when(userService.findById(eq(USER_ID), any(Context.class))).thenReturn(existingUser);
        doNothing().when(userService).update(any(User.class), any(Context.class));

        // Action
        ChangeUserPasswordUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isTrue();
        assertThat(response.userId()).isEqualTo(USER_ID);
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());

        verify(userService).findById(eq(USER_ID), any(Context.class));
        verify(userService).update(userCaptor.capture(), any(Context.class));
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("ENCODED_NEW");
    }

    @Test
    void WHEN_noAuthenticatedUser_THEN_throws() {
        when(currentUserProvider.requiredUserId())
                .thenThrow(new InsufficientAuthenticationException("test"));

        assertThrows(InsufficientAuthenticationException.class,
                () -> usecase.execute(new ChangeUserPasswordUseCase.Request(OLD_PASSWORD, NEW_PASSWORD)));

        verify(userService, never()).findById(anyString(), any(Context.class));
        verify(userService, never()).update(any(User.class), any(Context.class));
    }

    @Test
    void WHEN_execute_nullNewPassword_THEN_returnValidationError() {
        // Arrange
        ChangeUserPasswordUseCase.Request request = new ChangeUserPasswordUseCase.Request(
                OLD_PASSWORD, null
        );

        // Action
        ChangeUserPasswordUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.userId()).isEqualTo(USER_ID);
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.PASSWORD_REQUIRED.getMessage());

        verify(userService, never()).findById(anyString(), any(Context.class));
        verify(userService, never()).update(any(User.class), any(Context.class));
    }

    @Test
    void WHEN_execute_userNotFound_THEN_returnNotFoundError() {
        // Arrange
        ChangeUserPasswordUseCase.Request request = createValidRequest();
        when(userService.findById(eq(USER_ID), any(Context.class))).thenAnswer(invocation -> {
            Context ctx = invocation.getArgument(1);
            ctx.setError(ErrorCode.USER_NOT_FOUND);
            return null;
        });

        // Action
        ChangeUserPasswordUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.userId()).isEqualTo(USER_ID);
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());

        verify(userService).findById(eq(USER_ID), any(Context.class));
        verify(userService, never()).update(any(User.class), any(Context.class));
    }

    @Test
    void WHEN_execute_oldPasswordMismatch_THEN_returnPasswordMismatchError() {
        // Arrange
        ChangeUserPasswordUseCase.Request request = new ChangeUserPasswordUseCase.Request(
                "WrongOldPassword", NEW_PASSWORD
        );
        User existingUser = createUser(OLD_PASSWORD);

        when(passwordEncoder.matches(eq("WrongOldPassword"), eq(OLD_PASSWORD))).thenReturn(false);
        when(userService.findById(eq(USER_ID), any(Context.class))).thenReturn(existingUser);

        // Action
        ChangeUserPasswordUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.userId()).isEqualTo(USER_ID);
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.PASSWORDS_ARE_NOT_EQUAL.getMessage());

        verify(userService).findById(eq(USER_ID), any(Context.class));
        verify(userService, never()).update(any(User.class), any(Context.class));
    }

    @Test
    void WHEN_execute_saveThrowsException_THEN_propagateException() {
        // Arrange
        ChangeUserPasswordUseCase.Request request = createValidRequest();
        User existingUser = createUser(OLD_PASSWORD);

        when(userService.findById(eq(USER_ID), any(Context.class))).thenReturn(existingUser);
        doThrow(new RuntimeException("DB error")).when(userService).update(any(User.class), any(Context.class));

        // Action + Assert
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> usecase.execute(request));
    }

    @Test
    void WHEN_execute_validRequest_THEN_oldPasswordIsReplaced() {
        // Arrange
        ChangeUserPasswordUseCase.Request request = createValidRequest();
        User existingUser = createUser(OLD_PASSWORD);

        when(userService.findById(eq(USER_ID), any(Context.class))).thenReturn(existingUser);
        doNothing().when(userService).update(any(User.class), any(Context.class));

        // Action
        usecase.execute(request);

        // Asserts
        verify(userService).update(userCaptor.capture(), any(Context.class));
        assertThat(userCaptor.getValue().getPassword()).isNotEqualTo(OLD_PASSWORD);
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("ENCODED_NEW");
    }

    private ChangeUserPasswordUseCase.Request createValidRequest() {
        return new ChangeUserPasswordUseCase.Request(OLD_PASSWORD, NEW_PASSWORD);
    }

    private User createUser(String password) {
        return User.create(
                USER_ID, "user@example.com", "+79001234567", password,
                UserStatus.ACTIVE, null, null, null,
                ru.belov.ourabroad.core.security.AppRoles.DEFAULT,
                LocalDateTime.now(), null
        );
    }
}

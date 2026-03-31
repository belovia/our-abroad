package ru.belov.ourabroad.api.usecases.change.user.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.change.user.ChangeUserEmailUseCase;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.core.domain.UserFactory;
import ru.belov.ourabroad.core.enums.UserStatus;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.UserValidator;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                ChangeUserEmailUseCaseImpl.class,
                UserValidator.class
        }
)
class ChangeUserEmailUseCaseImplTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private ChangeUserEmailUseCaseImpl usecase;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private static final String USER_ID = "user-123";
    private static final String NEW_EMAIL = "new@example.com";

    @Test
    void contextCreated() {
        assertNotNull(usecase);
        assertNotNull(userService);
    }

    @Test
    void WHEN_execute_validRequest_THEN_updateEmailSuccessfully() {
        // Arrange
        ChangeUserEmailUseCase.Request request = createValidRequest();
        User existingUser = createUser();

        when(userService.findById(eq(USER_ID), any(Context.class))).thenReturn(existingUser);
        doNothing().when(userService).update(any(User.class), any(Context.class));

        // Action
        ChangeUserEmailUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isTrue();
        assertThat(response.userId()).isEqualTo(USER_ID);
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());

        verify(userService).findById(eq(USER_ID), any(Context.class));
        verify(userService).update(userCaptor.capture(), any(Context.class));
        assertThat(userCaptor.getValue().getEmail()).isEqualTo(NEW_EMAIL);
    }

    @Test
    void WHEN_execute_nullUserId_THEN_returnValidationError() {
        // Arrange
        ChangeUserEmailUseCase.Request request = new ChangeUserEmailUseCase.Request(null, NEW_EMAIL);

        // Action
        ChangeUserEmailUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.userId()).isNull();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.USER_ID_REQUIRED.getMessage());

        verify(userService, never()).findById(anyString(), any(Context.class));
        verify(userService, never()).update(any(User.class), any(Context.class));
    }

    @Test
    void WHEN_execute_nullEmail_THEN_returnValidationError() {
        // Arrange
        ChangeUserEmailUseCase.Request request = new ChangeUserEmailUseCase.Request(USER_ID, null);

        // Action
        ChangeUserEmailUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.userId()).isEqualTo(USER_ID);
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.EMAIL_REQUIRED.getMessage());

        verify(userService, never()).findById(anyString(), any(Context.class));
        verify(userService, never()).update(any(User.class), any(Context.class));
    }

    @Test
    void WHEN_execute_invalidEmailFormat_THEN_returnValidationError() {
        // Arrange
        ChangeUserEmailUseCase.Request request = new ChangeUserEmailUseCase.Request(USER_ID, "not-an-email");

        // Action
        ChangeUserEmailUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.EMAIL_INVALID_FORMAT.getMessage());

        verify(userService, never()).findById(anyString(), any(Context.class));
    }

    @Test
    void WHEN_execute_userNotFound_THEN_returnNotFoundError() {
        // Arrange
        ChangeUserEmailUseCase.Request request = createValidRequest();
        when(userService.findById(eq(USER_ID), any(Context.class))).thenAnswer(invocation -> {
            Context ctx = invocation.getArgument(1);
            ctx.setError(ErrorCode.USER_NOT_FOUND);
            return null;
        });

        // Action
        ChangeUserEmailUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.userId()).isEqualTo(USER_ID);
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());

        verify(userService).findById(eq(USER_ID), any(Context.class));
        verify(userService, never()).update(any(User.class), any(Context.class));
    }

    @Test
    void WHEN_execute_saveThrowsException_THEN_propagateException() {
        // Arrange
        ChangeUserEmailUseCase.Request request = createValidRequest();
        User existingUser = createUser();

        when(userService.findById(eq(USER_ID), any(Context.class))).thenReturn(existingUser);
        doThrow(new RuntimeException("DB error")).when(userService).update(any(User.class), any(Context.class));

        // Action + Assert
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> usecase.execute(request));
    }

    private ChangeUserEmailUseCase.Request createValidRequest() {
        return new ChangeUserEmailUseCase.Request(USER_ID, NEW_EMAIL);
    }

    private User createUser() {
        return User.create(
                USER_ID, "old@example.com", "+79001234567", "password",
                UserStatus.ACTIVE, null, null, null,
                LocalDateTime.now(), null
        );
    }
}

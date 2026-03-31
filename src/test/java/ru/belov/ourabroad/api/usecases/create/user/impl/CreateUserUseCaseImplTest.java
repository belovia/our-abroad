package ru.belov.ourabroad.api.usecases.create.user.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.create.user.CreateUserUseCase;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.UserValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                CreateUserUseCaseImpl.class,
                UserValidator.class
        }
)
class CreateUserUseCaseImplTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private CreateUserUseCaseImpl usecase;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private static final String EMAIL = "user@example.com";
    private static final String PHONE = "+79123456789";
    private static final String PASSWORD = "SecurePass1";

    @Test
    void contextCreated() {
        assertNotNull(usecase);
        assertNotNull(userService);
    }

    @Test
    void WHEN_execute_validRequest_THEN_createUserSuccessfully() {
        // Arrange
        CreateUserUseCase.Request request = createValidRequest();
        when(userService.existsByEmail(anyString(), eq(EMAIL), any(Context.class))).thenReturn(false);
        doNothing().when(userService).save(any(User.class), any(Context.class));

        // Action
        CreateUserUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isTrue();
        assertThat(response.userId()).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());

        verify(userService).save(userCaptor.capture(), any(Context.class));
        assertThat(userCaptor.getValue().getEmail()).isEqualTo(EMAIL);
    }

    @Test
    void WHEN_execute_nullEmail_THEN_returnValidationError() {
        // Arrange
        CreateUserUseCase.Request request = new CreateUserUseCase.Request(
                null, PHONE, PASSWORD, null, null, null
        );

        // Action
        CreateUserUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.EMAIL_REQUIRED.getMessage());

        verify(userService, never()).save(any(User.class), any(Context.class));
    }

    @Test
    void WHEN_execute_invalidEmailFormat_THEN_returnValidationError() {
        // Arrange
        CreateUserUseCase.Request request = new CreateUserUseCase.Request(
                "not-an-email", PHONE, PASSWORD, null, null, null
        );

        // Action
        CreateUserUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.EMAIL_INVALID_FORMAT.getMessage());

        verify(userService, never()).save(any(User.class), any(Context.class));
    }

    @Test
    void WHEN_execute_invalidPhoneFormat_THEN_returnValidationError() {
        // Arrange
        CreateUserUseCase.Request request = new CreateUserUseCase.Request(
                EMAIL, "123", PASSWORD, null, null, null
        );

        // Action
        CreateUserUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.PHONE_INVALID_FORMAT.getMessage());

        verify(userService, never()).save(any(User.class), any(Context.class));
    }

    @Test
    void WHEN_execute_nullPassword_THEN_returnValidationError() {
        // Arrange
        CreateUserUseCase.Request request = new CreateUserUseCase.Request(
                EMAIL, PHONE, null, null, null, null
        );

        // Action
        CreateUserUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.PASSWORD_REQUIRED.getMessage());

        verify(userService, never()).save(any(User.class), any(Context.class));
    }

    @Test
    void WHEN_execute_emailAlreadyExists_THEN_returnEmailAlreadyExistsError() {
        // Arrange
        CreateUserUseCase.Request request = createValidRequest();
        when(userService.existsByEmail(anyString(), eq(EMAIL), any(Context.class))).thenReturn(true);

        // Action
        CreateUserUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS.getMessage());

        verify(userService, never()).save(any(User.class), any(Context.class));
    }

    @Test
    void WHEN_execute_validRequest_THEN_generatedUserIdIsNotNull() {
        // Arrange
        CreateUserUseCase.Request request = createValidRequest();
        when(userService.existsByEmail(anyString(), eq(EMAIL), any(Context.class))).thenReturn(false);
        doNothing().when(userService).save(any(User.class), any(Context.class));

        // Action
        CreateUserUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.userId()).isNotBlank();
        verify(userService).save(any(User.class), any(Context.class));
    }

    @Test
    void WHEN_execute_validRequest_nullPhone_THEN_createUserSuccessfully() {
        // Arrange - phone is optional (validator only validates format if provided)
        CreateUserUseCase.Request request = new CreateUserUseCase.Request(
                EMAIL, null, PASSWORD, null, null, null
        );
        when(userService.existsByEmail(anyString(), eq(EMAIL), any(Context.class))).thenReturn(false);
        doNothing().when(userService).save(any(User.class), any(Context.class));

        // Action
        CreateUserUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isTrue();
    }

    private CreateUserUseCase.Request createValidRequest() {
        return new CreateUserUseCase.Request(EMAIL, PHONE, PASSWORD, null, null, null);
    }
}

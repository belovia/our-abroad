package ru.belov.ourabroad.api.usecases.create.verification.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.create.verification.CreateVerificationUseCase;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.api.usecases.services.verification.VerificationService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.UserFactory;
import ru.belov.ourabroad.core.enums.VerificationType;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.UserValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CreateVerificationUseCaseImpl.class, UserValidator.class})
class CreateVerificationUseCaseImplTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private VerificationService verificationService;

    @Autowired
    private CreateVerificationUseCase useCase;

    private static final String USER_ID = "user-1";

    @Test
    void WHEN_noDuplicate_THEN_saves() {
        var user = UserFactory.newUser(USER_ID, "a@b.c", "+79001112233", "Secret123", null, null, null);
        when(userService.findById(eq(USER_ID), any(Context.class))).thenReturn(user);
        when(verificationService.hasPendingDuplicate(eq(USER_ID), eq(VerificationType.EMAIL), eq((String) null)))
                .thenReturn(false);
        doNothing().when(verificationService).save(any(), any(Context.class));

        var response = useCase.execute(
                new CreateVerificationUseCase.Request(USER_ID, VerificationType.EMAIL, null)
        );

        assertThat(response.success()).isTrue();
        assertThat(response.verificationId()).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
        verify(verificationService).save(any(), any(Context.class));
    }

    @Test
    void WHEN_duplicatePending_THEN_error() {
        var user = UserFactory.newUser(USER_ID, "a@b.c", "+79001112233", "Secret123", null, null, null);
        when(userService.findById(eq(USER_ID), any(Context.class))).thenReturn(user);
        when(verificationService.hasPendingDuplicate(eq(USER_ID), eq(VerificationType.EMAIL), eq((String) null)))
                .thenReturn(true);

        var response = useCase.execute(
                new CreateVerificationUseCase.Request(USER_ID, VerificationType.EMAIL, null)
        );

        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.VERIFICATION_ALREADY_EXISTS.getMessage());
        verify(verificationService, never()).save(any(), any(Context.class));
    }
}

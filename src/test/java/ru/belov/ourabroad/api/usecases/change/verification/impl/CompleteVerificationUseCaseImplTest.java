package ru.belov.ourabroad.api.usecases.change.verification.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.change.verification.CompleteVerificationUseCase;
import ru.belov.ourabroad.api.usecases.services.verification.VerificationService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Verification;
import ru.belov.ourabroad.core.domain.VerificationFactory;
import ru.belov.ourabroad.core.enums.VerificationStatus;
import ru.belov.ourabroad.core.enums.VerificationType;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.FieldValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CompleteVerificationUseCaseImpl.class, FieldValidator.class})
class CompleteVerificationUseCaseImplTest {

    @MockitoBean
    private VerificationService verificationService;

    @Autowired
    private CompleteVerificationUseCase useCase;

    @Test
    void WHEN_pending_THEN_completes() {
        Verification v = VerificationFactory.newVerification("v1", "u1", VerificationType.EMAIL, null);
        when(verificationService.findById(eq("v1"), any(Context.class))).thenReturn(v);
        doNothing().when(verificationService).updateStatus(any(Verification.class), any(Context.class));

        var response = useCase.execute(new CompleteVerificationUseCase.Request("v1"));

        assertThat(response.success()).isTrue();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
        assertThat(v.getStatus()).isEqualTo(VerificationStatus.VERIFIED);
        verify(verificationService).updateStatus(eq(v), any(Context.class));
    }

    @Test
    void WHEN_notPending_THEN_error() {
        Verification v = Verification.create(
                "v1", "u1", VerificationType.EMAIL, null,
                VerificationStatus.VERIFIED,
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
        );
        when(verificationService.findById(eq("v1"), any(Context.class))).thenReturn(v);

        var response = useCase.execute(new CompleteVerificationUseCase.Request("v1"));

        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.VERIFICATION_NOT_PENDING.getMessage());
        verify(verificationService, never()).updateStatus(any(), any(Context.class));
    }
}

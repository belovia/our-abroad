package ru.belov.ourabroad.api.usecases.change.verification.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.change.verification.RejectVerificationUseCase;
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
@ContextConfiguration(classes = {RejectVerificationUseCaseImpl.class, FieldValidator.class})
class RejectVerificationUseCaseImplTest {

    @MockitoBean
    private VerificationService verificationService;

    @Autowired
    private RejectVerificationUseCase useCase;

    @Test
    void WHEN_pending_THEN_rejects() {
        Verification v = VerificationFactory.newVerification("v1", "u1", VerificationType.DOCUMENT, "x");
        when(verificationService.findById(eq("v1"), any(Context.class))).thenReturn(v);
        doNothing().when(verificationService).updateStatus(any(Verification.class), any(Context.class));

        var response = useCase.execute(new RejectVerificationUseCase.Request("v1"));

        assertThat(response.success()).isTrue();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
        assertThat(v.getStatus()).isEqualTo(VerificationStatus.REJECTED);
        verify(verificationService).updateStatus(eq(v), any(Context.class));
    }
}

package ru.belov.ourabroad.api.usecases.get.verification.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.get.verification.GetVerificationByIdUseCase;
import ru.belov.ourabroad.api.usecases.services.verification.VerificationService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Verification;
import ru.belov.ourabroad.core.domain.VerificationFactory;
import ru.belov.ourabroad.core.enums.VerificationType;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.FieldValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {GetVerificationByIdUseCaseImpl.class, FieldValidator.class})
class GetVerificationByIdUseCaseImplTest {

    @MockitoBean
    private VerificationService verificationService;

    @Autowired
    private GetVerificationByIdUseCase useCase;

    @Test
    void WHEN_found_THEN_success() {
        Verification v = VerificationFactory.newVerification("v1", "u1", VerificationType.EMAIL, null);
        when(verificationService.findById(eq("v1"), any(Context.class))).thenReturn(v);

        var response = useCase.execute(new GetVerificationByIdUseCase.Request("v1"));

        assertThat(response.success()).isTrue();
        assertThat(response.verification()).isEqualTo(v);
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
    }
}

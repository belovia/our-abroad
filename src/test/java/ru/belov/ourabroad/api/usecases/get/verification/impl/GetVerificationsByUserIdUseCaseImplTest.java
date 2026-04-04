package ru.belov.ourabroad.api.usecases.get.verification.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.get.verification.GetVerificationsByUserIdUseCase;
import ru.belov.ourabroad.api.usecases.services.verification.VerificationService;
import ru.belov.ourabroad.config.security.CurrentUserProvider;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Verification;
import ru.belov.ourabroad.core.domain.VerificationFactory;
import ru.belov.ourabroad.core.enums.VerificationType;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.UserValidator;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {GetVerificationsByUserIdUseCaseImpl.class, UserValidator.class})
class GetVerificationsByUserIdUseCaseImplTest {

    @MockitoBean
    private VerificationService verificationService;

    @MockitoBean
    private CurrentUserProvider currentUserProvider;

    @Autowired
    private GetVerificationsByUserIdUseCase useCase;

    private static final String USER_ID = "user-1";

    @BeforeEach
    void stubUser() {
        when(currentUserProvider.requiredUserId()).thenReturn(USER_ID);
    }

    @Test
    void WHEN_validUser_THEN_list() {
        Verification v = VerificationFactory.newVerification("v1", USER_ID, VerificationType.PHONE, null);
        when(verificationService.findByUserId(eq(USER_ID), any(Context.class))).thenReturn(List.of(v));

        var response = useCase.execute(new GetVerificationsByUserIdUseCase.Request());

        assertThat(response.success()).isTrue();
        assertThat(response.verifications()).containsExactly(v);
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
    }
}

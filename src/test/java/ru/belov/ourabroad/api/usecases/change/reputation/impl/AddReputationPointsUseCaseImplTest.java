package ru.belov.ourabroad.api.usecases.change.reputation.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.change.reputation.AddReputationPointsUseCase;
import ru.belov.ourabroad.api.usecases.services.reputation.ReputationService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Reputation;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.UserValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AddReputationPointsUseCaseImpl.class, UserValidator.class})
class AddReputationPointsUseCaseImplTest {

    @MockitoBean
    private ReputationService reputationService;

    @Autowired
    private AddReputationPointsUseCase useCase;

    private static final String USER_ID = "user-1";

    @Test
    void WHEN_positivePoints_THEN_updates() {
        Reputation rep = Reputation.create(USER_ID, 40, 1);
        when(reputationService.findByUserId(eq(USER_ID), any(Context.class))).thenReturn(rep);
        doNothing().when(reputationService).update(any(Reputation.class), any(Context.class));

        var response = useCase.execute(new AddReputationPointsUseCase.Request(USER_ID, 70));

        assertThat(response.success()).isTrue();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
        assertThat(rep.getScore()).isEqualTo(110);
        assertThat(rep.getLevel()).isEqualTo(2);
        verify(reputationService).update(eq(rep), any(Context.class));
    }

    @Test
    void WHEN_nonPositivePoints_THEN_validationError() {
        var response = useCase.execute(new AddReputationPointsUseCase.Request(USER_ID, 0));

        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.REPUTATION_POINTS_INVALID.getMessage());
        verify(reputationService, never()).findByUserId(anyString(), any(Context.class));
    }
}

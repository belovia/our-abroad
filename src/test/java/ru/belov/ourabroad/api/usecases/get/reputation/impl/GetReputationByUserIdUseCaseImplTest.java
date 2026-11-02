package ru.belov.ourabroad.api.usecases.get.reputation.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.get.reputation.GetReputationByUserIdUseCase;
import ru.belov.ourabroad.api.usecases.services.reputation.ReputationService;
import ru.belov.ourabroad.config.security.CurrentUserProvider;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Reputation;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.UserValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {GetReputationByUserIdUseCaseImpl.class, UserValidator.class})
class GetReputationByUserIdUseCaseImplTest {

    @MockitoBean
    private ReputationService reputationService;

    @MockitoBean
    private CurrentUserProvider currentUserProvider;

    @Autowired
    private GetReputationByUserIdUseCase useCase;

    private static final String USER_ID = "user-1";

    @BeforeEach
    void stubUser() {
        when(currentUserProvider.requiredUserId()).thenReturn(USER_ID);
    }

    @Test
    void contextCreated() {
        assertNotNull(useCase);
    }

    @Test
    void WHEN_validUser_THEN_returnsReputationAndSuccess() {
        Reputation rep = Reputation.create(USER_ID, 10, 1);
        when(reputationService.findByUserId(eq(USER_ID), any(Context.class))).thenReturn(rep);

        var response = useCase.execute(new GetReputationByUserIdUseCase.Request());

        assertThat(response.success()).isTrue();
        assertThat(response.reputation()).isEqualTo(rep);
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
    }

    @Test
    void WHEN_notFound_THEN_error() {
        when(reputationService.findByUserId(eq(USER_ID), any(Context.class))).thenAnswer(inv -> {
            Context ctx = inv.getArgument(1);
            ctx.setError(ErrorCode.REPUTATION_NOT_FOUND);
            return null;
        });

        var response = useCase.execute(new GetReputationByUserIdUseCase.Request());

        assertThat(response.success()).isFalse();
        assertThat(response.reputation()).isNull();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.REPUTATION_NOT_FOUND.getMessage());
    }
}

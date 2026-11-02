package ru.belov.ourabroad.api.usecases.create.reputation.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.create.reputation.CreateReputationUseCase;
import ru.belov.ourabroad.api.usecases.services.reputation.ReputationService;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.config.security.CurrentUserProvider;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.core.domain.UserFactory;
import ru.belov.ourabroad.core.enums.UserStatus;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.UserValidator;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CreateReputationUseCaseImpl.class, UserValidator.class})
class CreateReputationUseCaseImplTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ReputationService reputationService;

    @MockitoBean
    private CurrentUserProvider currentUserProvider;

    @Autowired
    private CreateReputationUseCase useCase;

    private static final String USER_ID = "user-1";

    @BeforeEach
    void stubUser() {
        when(currentUserProvider.requiredUserId()).thenReturn(USER_ID);
    }

    @Test
    void WHEN_userExistsAndNoReputation_THEN_creates() {
        User user = UserFactory.newUser(
                USER_ID, "a@b.c", "+79001112233", "Secret123", null, null, null
        );
        when(userService.findById(eq(USER_ID), any(Context.class))).thenReturn(user);
        when(reputationService.existsByUserId(USER_ID)).thenReturn(false);
        doNothing().when(reputationService).save(any(), any(Context.class));

        var response = useCase.execute(new CreateReputationUseCase.Request());

        assertThat(response.success()).isTrue();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
        verify(reputationService).save(any(), any(Context.class));
    }

    @Test
    void WHEN_reputationExists_THEN_conflict() {
        User user = User.create(
                USER_ID, "a@b.c", "+79001112233", "x", UserStatus.ACTIVE,
                null, null, null, ru.belov.ourabroad.core.security.AppRoles.DEFAULT,
                LocalDateTime.now(), null
        );
        when(userService.findById(eq(USER_ID), any(Context.class))).thenReturn(user);
        when(reputationService.existsByUserId(USER_ID)).thenReturn(true);

        var response = useCase.execute(new CreateReputationUseCase.Request());

        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.REPUTATION_ALREADY_EXISTS.getMessage());
        verify(reputationService, never()).save(any(), any(Context.class));
    }
}

package ru.belov.ourabroad.api.usecases.delete.specialistprofile.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.delete.specialistprofile.DeleteSpecialistProfileUseCase;
import ru.belov.ourabroad.api.usecases.services.specialistprofile.SpecialistProfileService;
import ru.belov.ourabroad.config.security.CurrentUserProvider;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistProfile;
import ru.belov.ourabroad.core.domain.SpecialistProfileFactory;
import ru.belov.ourabroad.web.validators.ErrorCode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DeleteSpecialistProfileUseCaseImpl.class)
class DeleteSpecialistProfileUseCaseImplTest {

    private static final String USER_ID = "user-1";
    private static final String OTHER_USER_ID = "user-2";
    private static final String PROFILE_ID = "sp-1";

    @MockitoBean
    private CurrentUserProvider currentUserProvider;

    @MockitoBean
    private SpecialistProfileService profileService;

    @Autowired
    private DeleteSpecialistProfileUseCase useCase;

    @BeforeEach
    void stubUser() {
        when(currentUserProvider.requiredUserId()).thenReturn(USER_ID);
    }

    @Test
    void WHEN_ownerDeletesOwnProfile_THEN_successAndDeleteCalled() {
        SpecialistProfile profile = SpecialistProfileFactory.fromDb(
                PROFILE_ID, USER_ID, "desc", true, 0.0, 0
        );
        when(profileService.findById(eq(PROFILE_ID), any(Context.class))).thenReturn(profile);
        doAnswer(invocation -> null).when(profileService).delete(eq(PROFILE_ID), any(Context.class));

        DeleteSpecialistProfileUseCase.Response response = useCase.delete(PROFILE_ID);

        assertThat(response.success()).isTrue();
        assertThat(response.message()).isEqualTo(ErrorCode.SUCCESS.getMessage());
        verify(profileService).delete(eq(PROFILE_ID), any(Context.class));
    }

    @Test
    void WHEN_nonOwnerDeletes_THEN_accessDeniedAndDeleteNotCalled() {
        SpecialistProfile profile = SpecialistProfileFactory.fromDb(
                PROFILE_ID, OTHER_USER_ID, "desc", true, 0.0, 0
        );
        when(profileService.findById(eq(PROFILE_ID), any(Context.class))).thenReturn(profile);

        DeleteSpecialistProfileUseCase.Response response = useCase.delete(PROFILE_ID);

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.ACCESS_DENIED.getMessage());
        verify(profileService, never()).delete(any(), any());
    }

    @Test
    void WHEN_profileNotFound_THEN_specialistNotFound() {
        when(profileService.findById(eq(PROFILE_ID), any(Context.class))).thenAnswer(invocation -> {
            Context context = invocation.getArgument(1);
            context.setError(ErrorCode.SPECIALIST_PROFILE_NOT_FOUND);
            return null;
        });

        DeleteSpecialistProfileUseCase.Response response = useCase.delete(PROFILE_ID);

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.SPECIALIST_PROFILE_NOT_FOUND.getMessage());
        verify(profileService, never()).delete(any(), any());
    }

    @Test
    void WHEN_blankProfileId_THEN_validationError() {
        DeleteSpecialistProfileUseCase.Response response = useCase.delete("  ");

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.REQUEST_VALIDATION_ERROR.getMessage());
        verify(profileService, never()).findById(any(), any());
        verify(profileService, never()).delete(any(), any());
    }
}

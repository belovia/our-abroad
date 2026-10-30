package ru.belov.ourabroad.api.usecases.change.profile.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.change.profile.UpdateMyProfileUseCase;
import ru.belov.ourabroad.api.usecases.services.profile.ProfileService;
import ru.belov.ourabroad.config.security.CurrentUserProvider;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Profile;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.UserValidator;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UpdateMyProfileUseCaseImpl.class, UserValidator.class})
class UpdateMyProfileUseCaseImplTest {

    @MockitoBean
    private ProfileService profileService;

    @MockitoBean
    private CurrentUserProvider currentUserProvider;

    @Autowired
    private UpdateMyProfileUseCase useCase;

    @BeforeEach
    void stubUser() {
        when(currentUserProvider.requiredUserId()).thenReturn("user-1");
        when(profileService.findByUserId(eq("user-1"), any(Context.class))).thenReturn(existing());
    }

    @Test
    void WHEN_onlyDisplayNameSent_THEN_otherFieldsStay() {
        var response = useCase.execute(new UpdateMyProfileUseCase.Request(
                "  Ana  ",
                null,
                null,
                null,
                null,
                null
        ));

        assertThat(response.success()).isTrue();
        ArgumentCaptor<Profile> saved = ArgumentCaptor.forClass(Profile.class);
        verify(profileService).update(saved.capture(), any(Context.class));
        Profile profile = saved.getValue();
        assertThat(profile.getDisplayName()).isEqualTo("Ana");
        assertThat(profile.getCity()).isEqualTo("Cordoba");
        assertThat(profile.getBio()).isEqualTo("old");
        assertThat(profile.getLanguages()).containsExactly("ru");
        assertThat(response.displayName()).isEqualTo("Ana");
        assertThat(response.city()).isEqualTo("Cordoba");
    }

    @Test
    void WHEN_displayNameTooShort_THEN_errorAndNoUpdate() {
        var response = useCase.execute(new UpdateMyProfileUseCase.Request(
                "A", null, null, null, null, null
        ));

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.DISPLAY_NAME_TOO_SHORT.getMessage());
        verify(profileService, never()).update(any(), any());
    }

    @Test
    void WHEN_displayNameTooLong_THEN_error() {
        var response = useCase.execute(new UpdateMyProfileUseCase.Request(
                "A".repeat(51), null, null, null, null, null
        ));

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.DISPLAY_NAME_TOO_LONG.getMessage());
        verify(profileService, never()).update(any(), any());
    }

    @Test
    void WHEN_bioTooLong_THEN_error() {
        var response = useCase.execute(new UpdateMyProfileUseCase.Request(
                null, null, "b".repeat(501), null, null, null
        ));

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.BIO_TOO_LONG.getMessage());
        verify(profileService, never()).update(any(), any());
    }

    @Test
    void WHEN_languagesEmpty_THEN_clearsLanguages() {
        useCase.execute(new UpdateMyProfileUseCase.Request(
                null, null, null, null, null, Set.of()
        ));

        ArgumentCaptor<Profile> saved = ArgumentCaptor.forClass(Profile.class);
        verify(profileService).update(saved.capture(), any(Context.class));
        assertThat(saved.getValue().getLanguages()).isEmpty();
    }

    @Test
    void WHEN_profileMissing_THEN_notFound() {
        when(profileService.findByUserId(eq("user-1"), any(Context.class))).thenAnswer(inv -> {
            Context ctx = inv.getArgument(1);
            ctx.setError(ErrorCode.PROFILE_NOT_FOUND);
            return null;
        });

        var response = useCase.execute(new UpdateMyProfileUseCase.Request(
                "Ana", null, null, null, null, null
        ));

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.PROFILE_NOT_FOUND.getMessage());
        verify(profileService, never()).update(any(), any());
    }

    private static Profile existing() {
        return Profile.create(
                "user-1",
                "Old",
                null,
                "old",
                "AR",
                "Cordoba",
                Set.of("ru")
        );
    }
}

package ru.belov.ourabroad.api.usecases.get.profile.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.get.profile.GetMyProfileUseCase;
import ru.belov.ourabroad.api.usecases.services.profile.ProfileService;
import ru.belov.ourabroad.config.security.CurrentUserProvider;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Profile;
import ru.belov.ourabroad.web.validators.ErrorCode;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = GetMyProfileUseCaseImpl.class)
class GetMyProfileUseCaseImplTest {

    @MockitoBean
    private ProfileService profileService;

    @MockitoBean
    private CurrentUserProvider currentUserProvider;

    @Autowired
    private GetMyProfileUseCase useCase;

    @BeforeEach
    void stubUser() {
        when(currentUserProvider.requiredUserId()).thenReturn("user-1");
    }

    @Test
    void WHEN_profileExists_THEN_returnsFields() {
        Profile profile = Profile.create(
                "user-1",
                "Ana",
                "https://cdn.example/a.png",
                "hi",
                "AR",
                "Buenos Aires",
                Set.of("ru", "es")
        );
        when(profileService.findByUserId(eq("user-1"), any(Context.class))).thenReturn(profile);

        var response = useCase.execute();

        assertThat(response.success()).isTrue();
        assertThat(response.message()).isEqualTo(ErrorCode.SUCCESS.getMessage());
        assertThat(response.userId()).isEqualTo("user-1");
        assertThat(response.displayName()).isEqualTo("Ana");
        assertThat(response.avatarUrl()).isEqualTo("https://cdn.example/a.png");
        assertThat(response.bio()).isEqualTo("hi");
        assertThat(response.country()).isEqualTo("AR");
        assertThat(response.city()).isEqualTo("Buenos Aires");
        assertThat(response.languages()).containsExactlyInAnyOrder("ru", "es");
    }

    @Test
    void WHEN_profileMissing_THEN_notFound() {
        when(profileService.findByUserId(eq("user-1"), any(Context.class))).thenAnswer(inv -> {
            Context ctx = inv.getArgument(1);
            ctx.setError(ErrorCode.PROFILE_NOT_FOUND);
            return null;
        });

        var response = useCase.execute();

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.PROFILE_NOT_FOUND.getMessage());
        assertThat(response.displayName()).isNull();
    }
}

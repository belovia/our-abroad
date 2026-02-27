package ru.belov.ourabroad.api.usecases.change.specialistprofile.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.change.specialistprofile.ChangeSpecialistProfileUseCase;
import ru.belov.ourabroad.api.usecases.services.specialistprofile.SpecialistProfileService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistProfile;
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.poi.storage.SpecialistProfileRepository;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.SpecialistProfileValidator;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                ChangeSpecialistProfileUseCaseImpl.class,
                SpecialistProfileValidator.class
        }
)
class ChangeSpecialistProfileUseCaseImplTest {

    @MockitoBean
    private SpecialistProfileRepository repository;

    @MockitoBean
    private SpecialistProfileService service;

    @Autowired
    private ChangeSpecialistProfileUseCaseImpl usecase;

    @Captor
    private ArgumentCaptor<Context> contextCaptor;

    @Captor
    private ArgumentCaptor<SpecialistProfile> profileCaptor;

    private static final String PROFILE_ID = "profile-123";
    private static final String USER_ID = "user-456";
    private static final String DESCRIPTION = "Updated description";
    private static final String OLD_DESCRIPTION = "Old description";

    @Test
    void contextCreated() {
        assertNotNull(usecase);
        assertNotNull(service);
        assertNotNull(repository);
    }

    @Test
    void WHEN_execute_validRequest_THEN_updateProfileSuccessfully() {
        // Arrange
        ChangeSpecialistProfileUseCase.Request request = createValidRequest();
        SpecialistProfile existingProfile = createSpecialistProfile(OLD_DESCRIPTION);

        when(service.findById(eq(PROFILE_ID), any(Context.class)))
                .thenReturn(existingProfile);
        doNothing().when(service).update(any(SpecialistProfile.class));

        // Action
        ChangeSpecialistProfileUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isTrue();
        assertThat(response.profileId()).isEqualTo(PROFILE_ID);
        assertThat(response.message()).isNull();

        verify(service).findById(eq(PROFILE_ID), any(Context.class));
        verify(service).update(profileCaptor.capture());

        SpecialistProfile updatedProfile = profileCaptor.getValue();
        assertThat(updatedProfile.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(updatedProfile.getId()).isEqualTo(PROFILE_ID);
    }

    @Test
    void WHEN_execute_nullProfileId_THEN_returnValidationError() {
        // Arrange
        ChangeSpecialistProfileUseCase.Request request = new ChangeSpecialistProfileUseCase.Request(
                null, // profileId is null
                DESCRIPTION,
                new HashSet<>()
        );

        // Action
        ChangeSpecialistProfileUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.profileId()).isNull();
        assertThat(response.message()).isEqualTo(ErrorCode.FIELD_REQUIRED.getMessage());


        verify(service, never()).findById(anyString(), any(Context.class));
        verify(service, never()).update(any(SpecialistProfile.class));
    }

    @Test
    void WHEN_execute_nullDescription_THEN_returnValidationError() {
        // Arrange
        ChangeSpecialistProfileUseCase.Request request = new ChangeSpecialistProfileUseCase.Request(
                PROFILE_ID,
                null, // description is null
                new HashSet<>()
        );

        // Action
        ChangeSpecialistProfileUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.profileId()).isEqualTo(PROFILE_ID);
        assertThat(response.message()).isEqualTo(ErrorCode.FIELD_REQUIRED.getMessage());

        verify(service, never()).findById(anyString(), any(Context.class));
        verify(service, never()).update(any(SpecialistProfile.class));
    }

    @Test
    void WHEN_execute_profileNotFound_THEN_returnNotFoundError() {
        // Arrange
        ChangeSpecialistProfileUseCase.Request request = createValidRequest();

        when(service.findById(eq(PROFILE_ID), any(Context.class)))
                .thenAnswer(invocation -> {
                    Context context = invocation.getArgument(1);
                    context.setError(ErrorCode.SPECIALIST_PROFILE_NOT_FOUND);
                    return null;
                });

        // Action
        ChangeSpecialistProfileUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.profileId()).isEqualTo(PROFILE_ID);
        assertThat(response.message()).isEqualTo(ErrorCode.SPECIALIST_PROFILE_NOT_FOUND.getMessage());

        verify(service).findById(eq(PROFILE_ID), any(Context.class));
        verify(service, never()).update(any(SpecialistProfile.class));
    }

    @Test
    void WHEN_execute_updateThrowsException_THEN_returnDbError() {
        // Arrange
        ChangeSpecialistProfileUseCase.Request request = createValidRequest();
        SpecialistProfile existingProfile = createSpecialistProfile(OLD_DESCRIPTION);

        when(service.findById(eq(PROFILE_ID), any(Context.class)))
                .thenReturn(existingProfile);
        doThrow(new RuntimeException("Database error"))
                .when(service).update(any(SpecialistProfile.class));

        // Action
        ChangeSpecialistProfileUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.profileId()).isEqualTo(PROFILE_ID);
        assertThat(response.message()).isEqualTo(ErrorCode.DB_ERROR.getMessage());

        verify(service).findById(eq(PROFILE_ID), any(Context.class));
        verify(service).update(any(SpecialistProfile.class));
    }

    @Test
    void WHEN_execute_blankDescription_THEN_returnValidationError() {
        // Arrange
        ChangeSpecialistProfileUseCase.Request request = new ChangeSpecialistProfileUseCase.Request(
                PROFILE_ID,
                "   ", // blank description
                new HashSet<>()
        );

        SpecialistProfile existingProfile = createSpecialistProfile(OLD_DESCRIPTION);

        when(service.findById(eq(PROFILE_ID), any(Context.class)))
                .thenReturn(existingProfile);
        doNothing().when(service).update(any(SpecialistProfile.class));

        // Action
        ChangeSpecialistProfileUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.profileId()).isEqualTo(PROFILE_ID);
        assertThat(response.message()).isEqualTo(ErrorCode.FIELD_REQUIRED.getMessage());
    }

    @Test
    void WHEN_execute_validRequest_THEN_preserveOtherFields() {
        // Arrange
        ChangeSpecialistProfileUseCase.Request request = createValidRequest();

        Set<SpecialistService> originalServices = new HashSet<>();
        originalServices.add(new SpecialistService("service-1", "Service 1", "Title", "Description", 100, "USD", false));

        SpecialistProfile existingProfile = SpecialistProfile.builder()
                .id(PROFILE_ID)
                .userId(USER_ID)
                .description(OLD_DESCRIPTION)
                .active(true)
                .rating(4.5)
                .reviewsCount(10)
                .services(originalServices)
                .build();

        when(service.findById(eq(PROFILE_ID), any(Context.class)))
                .thenReturn(existingProfile);
        doNothing().when(service).update(any(SpecialistProfile.class));

        // Action
        usecase.execute(request);

        // Asserts
        verify(service).update(profileCaptor.capture());
        SpecialistProfile updatedProfile = profileCaptor.getValue();

        assertThat(updatedProfile.getId()).isEqualTo(PROFILE_ID);
        assertThat(updatedProfile.getUserId()).isEqualTo(USER_ID);
        assertThat(updatedProfile.isActive()).isTrue();
        assertThat(updatedProfile.getRating()).isEqualTo(4.5);
        assertThat(updatedProfile.getReviewsCount()).isEqualTo(10);
        assertThat(updatedProfile.getServices()).isEqualTo(originalServices);
        assertThat(updatedProfile.getDescription()).isEqualTo(DESCRIPTION);
    }

    @Test
    void WHEN_execute_validationFails_THEN_skipRetrieveAndUpdate() {
        // Arrange
        ChangeSpecialistProfileUseCase.Request request = new ChangeSpecialistProfileUseCase.Request(
                null,
                DESCRIPTION,
                new HashSet<>()
        );

        // Action
        ChangeSpecialistProfileUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();

        verify(service, never()).findById(anyString(), any(Context.class));
        verify(service, never()).update(any(SpecialistProfile.class));
    }

    @Test
    void WHEN_execute_multipleValidationErrors_THEN_returnFirstError() {
        // Arrange
        ChangeSpecialistProfileUseCase.Request request = new ChangeSpecialistProfileUseCase.Request(
                null,
                null,
                new HashSet<>()
        );

        // Action
        ChangeSpecialistProfileUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.FIELD_REQUIRED.getMessage());

        verify(service, never()).findById(anyString(), any(Context.class));
    }

    private ChangeSpecialistProfileUseCase.Request createValidRequest() {
        return new ChangeSpecialistProfileUseCase.Request(
                PROFILE_ID,
                DESCRIPTION,
                new HashSet<>()
        );
    }

    private SpecialistProfile createSpecialistProfile(String description) {
        return SpecialistProfile.builder()
                .id(PROFILE_ID)
                .userId(USER_ID)
                .description(description)
                .active(true)
                .rating(0.0)
                .reviewsCount(0)
                .services(new HashSet<>())
                .build();
    }
}
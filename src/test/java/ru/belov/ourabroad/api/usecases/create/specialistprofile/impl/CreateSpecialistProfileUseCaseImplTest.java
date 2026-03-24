package ru.belov.ourabroad.api.usecases.create.specialistprofile.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.create.specialistprofile.CreateSpecialistProfileUseCase;
import ru.belov.ourabroad.api.usecases.services.specialistprofile.SpecialistProfileService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistProfile;
import ru.belov.ourabroad.web.validators.ErrorCode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                CreateSpecialistProfileUseCaseImpl.class
        }
)
class CreateSpecialistProfileUseCaseImplTest {

    @MockitoBean
    private SpecialistProfileService profileService;

    @Autowired
    private CreateSpecialistProfileUseCaseImpl usecase;

    @Captor
    private ArgumentCaptor<SpecialistProfile> profileCaptor;

    private static final String USER_ID = "user-123";
    private static final String DESCRIPTION = "Expert in Java development";

    @Test
    void contextCreated() {
        assertNotNull(usecase);
        assertNotNull(profileService);
    }

    @Test
    void WHEN_execute_validRequest_THEN_createProfileSuccessfully() {
        // Arrange
        CreateSpecialistProfileUseCase.Request request = createValidRequest();
        doNothing().when(profileService).save(any(SpecialistProfile.class), any(Context.class));

        // Action
        CreateSpecialistProfileUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isTrue();
        assertThat(response.userId()).isEqualTo(USER_ID);
        assertThat(response.errorMessage()).isNull();

        verify(profileService).save(profileCaptor.capture(), any(Context.class));
        SpecialistProfile savedProfile = profileCaptor.getValue();
        assertThat(savedProfile.getUserId()).isEqualTo(USER_ID);
    }

    @Test
    void WHEN_execute_nullUserId_THEN_returnValidationError() {
        // Arrange
        CreateSpecialistProfileUseCase.Request request = new CreateSpecialistProfileUseCase.Request(
                null,
                DESCRIPTION
        );

        // Action
        CreateSpecialistProfileUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.userId()).isNull();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.USER_ID_REQUIRED.getMessage());

        verify(profileService, never()).save(any(SpecialistProfile.class), any(Context.class));
    }

    @Test
    void WHEN_execute_blankUserId_THEN_returnValidationError() {
        // Arrange
        CreateSpecialistProfileUseCase.Request request = new CreateSpecialistProfileUseCase.Request(
                "   ",
                DESCRIPTION
        );

        // Action
        CreateSpecialistProfileUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.USER_ID_REQUIRED.getMessage());

        verify(profileService, never()).save(any(SpecialistProfile.class), any(Context.class));
    }

    @Test
    void WHEN_execute_saveThrowsException_THEN_propagateException() {
        // Arrange
        CreateSpecialistProfileUseCase.Request request = createValidRequest();
        doThrow(new RuntimeException("DB error")).when(profileService).save(any(SpecialistProfile.class), any(Context.class));

        // Action + Assert
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> usecase.execute(request));

        verify(profileService).save(any(SpecialistProfile.class), any(Context.class));
    }

    @Test
    void WHEN_execute_validRequest_nullDescription_THEN_createProfileSuccessfully() {
        // Arrange
        CreateSpecialistProfileUseCase.Request request = new CreateSpecialistProfileUseCase.Request(
                USER_ID,
                null
        );
        doNothing().when(profileService).save(any(SpecialistProfile.class), any(Context.class));

        // Action
        CreateSpecialistProfileUseCase.Response response = usecase.execute(request);

        // Asserts — description is optional, userId is required
        assertThat(response.success()).isTrue();
        assertThat(response.userId()).isEqualTo(USER_ID);
    }

    @Test
    void WHEN_execute_validRequest_THEN_profileSavedWithCorrectUserId() {
        // Arrange
        CreateSpecialistProfileUseCase.Request request = createValidRequest();
        doNothing().when(profileService).save(any(SpecialistProfile.class), any(Context.class));

        // Action
        usecase.execute(request);

        // Asserts
        verify(profileService).save(profileCaptor.capture(), any(Context.class));
        assertThat(profileCaptor.getValue().getUserId()).isEqualTo(USER_ID);
    }

    private CreateSpecialistProfileUseCase.Request createValidRequest() {
        return new CreateSpecialistProfileUseCase.Request(USER_ID, DESCRIPTION);
    }
}

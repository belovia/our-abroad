package ru.belov.ourabroad.api.usecases.get.specialistprofile.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.get.specialistprofile.GetSpecialistProfileByUserIdUseCase;
import ru.belov.ourabroad.api.usecases.get.specialistservice.GetServicesByProfileIdUseCase;
import ru.belov.ourabroad.api.usecases.services.specialistprofile.SpecialistProfileService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistProfile;
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.FieldValidator;

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
                GetSpecialistProfileByUserIdUseCaseImpl.class,
                FieldValidator.class
        }
)
class GetSpecialistProfileByUserIdUseCaseImplTest {

    @MockitoBean
    private SpecialistProfileService service;

    @MockitoBean
    private GetServicesByProfileIdUseCase getSpecialistServicesUsecase;

    @Autowired
    private GetSpecialistProfileByUserIdUseCaseImpl usecase;

    private static final String USER_ID = "user-123";
    private static final String PROFILE_ID = "profile-456";

    @Test
    void contextCreated() {
        assertNotNull(usecase);
        assertNotNull(service);
        assertNotNull(getSpecialistServicesUsecase);
    }

    @Test
    void WHEN_execute_validRequest_THEN_returnProfileSuccessfully() {
        // Arrange
        GetSpecialistProfileByUserIdUseCase.Request request = new GetSpecialistProfileByUserIdUseCase.Request(USER_ID);
        SpecialistProfile profile = createProfile();
        Set<SpecialistService> services = Set.of(new SpecialistService("s-1", PROFILE_ID, "Title", "Desc", 100, "USD", true));

        when(service.findByUserId(eq(USER_ID), any(Context.class))).thenReturn(profile);
        when(getSpecialistServicesUsecase.execute(any())).thenReturn(
                new GetServicesByProfileIdUseCase.Response(services, true, null)
        );

        // Action
        GetSpecialistProfileByUserIdUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isTrue();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
        assertThat(response.specialist()).isNotNull();
        assertThat(response.specialist().getUserId()).isEqualTo(USER_ID);
        assertThat(response.specialist().getServices()).isEqualTo(services);

        verify(service).findByUserId(eq(USER_ID), any(Context.class));
        verify(getSpecialistServicesUsecase).execute(any());
    }

    @Test
    void WHEN_execute_nullUserId_THEN_returnValidationError() {
        // Arrange
        GetSpecialistProfileByUserIdUseCase.Request request = new GetSpecialistProfileByUserIdUseCase.Request(null);

        // Action
        GetSpecialistProfileByUserIdUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.FIELD_REQUIRED.getMessage());
        assertThat(response.specialist()).isNull();

        verify(service, never()).findByUserId(anyString(), any(Context.class));
        verify(getSpecialistServicesUsecase, never()).execute(any());
    }

    @Test
    void WHEN_execute_blankUserId_THEN_returnValidationError() {
        // Arrange
        GetSpecialistProfileByUserIdUseCase.Request request = new GetSpecialistProfileByUserIdUseCase.Request("   ");

        // Action
        GetSpecialistProfileByUserIdUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.FIELD_REQUIRED.getMessage());

        verify(service, never()).findByUserId(anyString(), any(Context.class));
    }

    @Test
    void WHEN_execute_profileNotFound_THEN_returnNotFoundError() {
        // Arrange
        GetSpecialistProfileByUserIdUseCase.Request request = new GetSpecialistProfileByUserIdUseCase.Request(USER_ID);

        when(service.findByUserId(eq(USER_ID), any(Context.class)))
                .thenAnswer(invocation -> {
                    Context context = invocation.getArgument(1);
                    context.setError(ErrorCode.SPECIALIST_PROFILE_NOT_FOUND);
                    return null;
                });

        // Action
        GetSpecialistProfileByUserIdUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.SPECIALIST_PROFILE_NOT_FOUND.getMessage());
        assertThat(response.specialist()).isNull();

        verify(service).findByUserId(eq(USER_ID), any(Context.class));
        verify(getSpecialistServicesUsecase, never()).execute(any());
    }

    @Test
    void WHEN_execute_validRequest_THEN_servicesAreLoadedIntoProfile() {
        // Arrange
        GetSpecialistProfileByUserIdUseCase.Request request = new GetSpecialistProfileByUserIdUseCase.Request(USER_ID);
        SpecialistProfile profile = createProfile();
        Set<SpecialistService> services = new HashSet<>();
        services.add(new SpecialistService("s-1", PROFILE_ID, "Title", "Desc", 100, "USD", true));
        services.add(new SpecialistService("s-2", PROFILE_ID, "Title 2", "Desc 2", 200, "USD", true));

        when(service.findByUserId(eq(USER_ID), any(Context.class))).thenReturn(profile);
        when(getSpecialistServicesUsecase.execute(any())).thenReturn(
                new GetServicesByProfileIdUseCase.Response(services, true, null)
        );

        // Action
        GetSpecialistProfileByUserIdUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.specialist().getServices()).hasSize(2);
    }

    private SpecialistProfile createProfile() {
        return SpecialistProfile.builder()
                .id(PROFILE_ID)
                .userId(USER_ID)
                .description("Test description")
                .active(true)
                .rating(4.0)
                .reviewsCount(5)
                .services(new HashSet<>())
                .build();
    }
}

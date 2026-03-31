package ru.belov.ourabroad.api.usecases.get.specialistprofile.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.get.specialistprofile.GetSpecialistProfileByIdUseCase;
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
                GetSpecialistProfileByIdUseCaseImpl.class,
                FieldValidator.class
        }
)
class GetSpecialistProfileByIdUseCaseImplTest {

    @MockitoBean
    private SpecialistProfileService service;

    @MockitoBean
    private GetServicesByProfileIdUseCase getServiceUseCase;

    @Autowired
    private GetSpecialistProfileByIdUseCaseImpl usecase;

    private static final String PROFILE_ID = "profile-123";
    private static final String USER_ID = "user-456";

    @Test
    void contextCreated() {
        assertNotNull(usecase);
        assertNotNull(service);
        assertNotNull(getServiceUseCase);
    }

    @Test
    void WHEN_execute_validRequest_THEN_returnProfileSuccessfully() {
        // Arrange
        GetSpecialistProfileByIdUseCase.Request request = new GetSpecialistProfileByIdUseCase.Request(PROFILE_ID);
        SpecialistProfile profile = createProfile();
        Set<SpecialistService> services = Set.of(new SpecialistService("s-1", PROFILE_ID, "Title", "Desc", 100, "USD", true));

        when(service.findById(eq(PROFILE_ID), any(Context.class))).thenReturn(profile);
        when(getServiceUseCase.execute(any())).thenReturn(
                new GetServicesByProfileIdUseCase.Response(services, true, null)
        );

        // Action
        GetSpecialistProfileByIdUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isTrue();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
        assertThat(response.specialist()).isNotNull();
        assertThat(response.specialist().getId()).isEqualTo(PROFILE_ID);
        assertThat(response.specialist().getServices()).isEqualTo(services);

        verify(service).findById(eq(PROFILE_ID), any(Context.class));
        verify(getServiceUseCase).execute(any());
    }

    @Test
    void WHEN_execute_nullProfileId_THEN_returnValidationError() {
        // Arrange
        GetSpecialistProfileByIdUseCase.Request request = new GetSpecialistProfileByIdUseCase.Request(null);

        // Action
        GetSpecialistProfileByIdUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.FIELD_REQUIRED.getMessage());
        assertThat(response.specialist()).isNull();

        verify(service, never()).findById(anyString(), any(Context.class));
        verify(getServiceUseCase, never()).execute(any());
    }

    @Test
    void WHEN_execute_blankProfileId_THEN_returnValidationError() {
        // Arrange
        GetSpecialistProfileByIdUseCase.Request request = new GetSpecialistProfileByIdUseCase.Request("   ");

        // Action
        GetSpecialistProfileByIdUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.FIELD_REQUIRED.getMessage());

        verify(service, never()).findById(anyString(), any(Context.class));
    }

    @Test
    void WHEN_execute_profileNotFound_THEN_returnNotFoundError() {
        // Arrange
        GetSpecialistProfileByIdUseCase.Request request = new GetSpecialistProfileByIdUseCase.Request(PROFILE_ID);

        when(service.findById(eq(PROFILE_ID), any(Context.class)))
                .thenAnswer(invocation -> {
                    Context context = invocation.getArgument(1);
                    context.setError(ErrorCode.SPECIALIST_PROFILE_NOT_FOUND);
                    return null;
                });

        // Action
        GetSpecialistProfileByIdUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.SPECIALIST_PROFILE_NOT_FOUND.getMessage());
        assertThat(response.specialist()).isNull();

        verify(service).findById(eq(PROFILE_ID), any(Context.class));
        verify(getServiceUseCase, never()).execute(any());
    }

    @Test
    void WHEN_execute_validRequest_THEN_servicesAreLoadedIntoProfile() {
        // Arrange
        GetSpecialistProfileByIdUseCase.Request request = new GetSpecialistProfileByIdUseCase.Request(PROFILE_ID);
        SpecialistProfile profile = createProfile();
        Set<SpecialistService> services = new HashSet<>();
        services.add(new SpecialistService("s-1", PROFILE_ID, "Title", "Desc", 100, "USD", true));
        services.add(new SpecialistService("s-2", PROFILE_ID, "Title 2", "Desc 2", 200, "USD", true));

        when(service.findById(eq(PROFILE_ID), any(Context.class))).thenReturn(profile);
        when(getServiceUseCase.execute(any())).thenReturn(
                new GetServicesByProfileIdUseCase.Response(services, true, null)
        );

        // Action
        GetSpecialistProfileByIdUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.specialist().getServices()).hasSize(2);
    }

    private SpecialistProfile createProfile() {
        return SpecialistProfile.builder()
                .id(PROFILE_ID)
                .userId(USER_ID)
                .description("Test description")
                .active(true)
                .rating(4.5)
                .reviewsCount(10)
                .services(new HashSet<>())
                .build();
    }
}

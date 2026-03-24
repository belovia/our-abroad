package ru.belov.ourabroad.api.usecases.get.specialistservice.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.get.specialistservice.GetServicesByProfileIdUseCase;
import ru.belov.ourabroad.api.usecases.services.specialistservice.SpecialistServiceService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.FieldValidator;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                GetServicesByProfileIdServiceImpl.class,
                FieldValidator.class
        }
)
class GetServicesByProfileIdServiceImplTest {

    @MockitoBean
    private SpecialistServiceService service;

    @Autowired
    private GetServicesByProfileIdServiceImpl usecase;

    private static final String PROFILE_ID = "profile-123";

    @Test
    void contextCreated() {
        assertNotNull(usecase);
        assertNotNull(service);
    }

    @Test
    void WHEN_execute_validRequest_THEN_returnServicesSuccessfully() {
        // Arrange
        GetServicesByProfileIdUseCase.Request request = new GetServicesByProfileIdUseCase.Request(PROFILE_ID);
        Set<SpecialistService> services = Set.of(
                new SpecialistService("s-1", PROFILE_ID, "Title 1", "Desc", 100, "USD", true),
                new SpecialistService("s-2", PROFILE_ID, "Title 2", "Desc", 200, "USD", true)
        );

        when(service.findAllById(eq(PROFILE_ID), any(Context.class))).thenReturn(services);

        // Action
        GetServicesByProfileIdUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isTrue();
        assertThat(response.errorMessage()).isNull();
        assertThat(response.services()).hasSize(2);

        verify(service).findAllById(eq(PROFILE_ID), any(Context.class));
    }

    @Test
    void WHEN_execute_nullProfileId_THEN_returnValidationError() {
        // Arrange
        GetServicesByProfileIdUseCase.Request request = new GetServicesByProfileIdUseCase.Request(null);

        // Action
        GetServicesByProfileIdUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.FIELD_REQUIRED.getMessage());
        assertThat(response.services()).isNull();

        verify(service, never()).findAllById(anyString(), any(Context.class));
    }

    @Test
    void WHEN_execute_blankProfileId_THEN_returnValidationError() {
        // Arrange
        GetServicesByProfileIdUseCase.Request request = new GetServicesByProfileIdUseCase.Request("   ");

        // Action
        GetServicesByProfileIdUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.FIELD_REQUIRED.getMessage());

        verify(service, never()).findAllById(anyString(), any(Context.class));
    }

    @Test
    void WHEN_execute_profileHasNoServices_THEN_returnEmptySet() {
        // Arrange
        GetServicesByProfileIdUseCase.Request request = new GetServicesByProfileIdUseCase.Request(PROFILE_ID);

        when(service.findAllById(eq(PROFILE_ID), any(Context.class))).thenReturn(Set.of());

        // Action
        GetServicesByProfileIdUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isTrue();
        assertThat(response.services()).isEmpty();
    }

    @Test
    void WHEN_execute_serviceThrowsError_THEN_returnDbError() {
        // Arrange
        GetServicesByProfileIdUseCase.Request request = new GetServicesByProfileIdUseCase.Request(PROFILE_ID);

        when(service.findAllById(eq(PROFILE_ID), any(Context.class)))
                .thenAnswer(invocation -> {
                    Context context = invocation.getArgument(1);
                    context.setError(ErrorCode.DB_ERROR);
                    return null;
                });

        // Action
        GetServicesByProfileIdUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.DB_ERROR.getMessage());
    }
}

package ru.belov.ourabroad.api.usecases.get.specialistservice.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.get.specialistservice.GetSpecialistServiceByServiceIdUseCase;
import ru.belov.ourabroad.api.usecases.services.specialistservice.SpecialistServiceService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.FieldValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                GetSpecialistServiceByServiceIdUseCaseImpl.class,
                FieldValidator.class
        }
)
class GetSpecialistServiceByServiceIdUseCaseImplTest {

    @MockitoBean
    private SpecialistServiceService service;

    @Autowired
    private GetSpecialistServiceByServiceIdUseCaseImpl usecase;

    private static final String SERVICE_ID = "service-123";
    private static final String PROFILE_ID = "profile-456";

    @Test
    void contextCreated() {
        assertNotNull(usecase);
        assertNotNull(service);
    }

    @Test
    void WHEN_execute_validRequest_THEN_returnServiceSuccessfully() {
        // Arrange
        GetSpecialistServiceByServiceIdUseCase.Request request = new GetSpecialistServiceByServiceIdUseCase.Request(SERVICE_ID);
        SpecialistService specialistService = createSpecialistService();

        when(service.findById(eq(SERVICE_ID), any(Context.class))).thenReturn(specialistService);

        // Action
        GetSpecialistServiceByServiceIdUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isTrue();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
        assertThat(response.service()).isNotNull();
        assertThat(response.service().getId()).isEqualTo(SERVICE_ID);

        verify(service).findById(eq(SERVICE_ID), any(Context.class));
    }

    @Test
    void WHEN_execute_nullServiceId_THEN_returnValidationError() {
        // Arrange
        GetSpecialistServiceByServiceIdUseCase.Request request = new GetSpecialistServiceByServiceIdUseCase.Request(null);

        // Action
        GetSpecialistServiceByServiceIdUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.FIELD_REQUIRED.getMessage());
        assertThat(response.service()).isNull();

        verify(service, never()).findById(anyString(), any(Context.class));
    }

    @Test
    void WHEN_execute_blankServiceId_THEN_returnValidationError() {
        // Arrange
        GetSpecialistServiceByServiceIdUseCase.Request request = new GetSpecialistServiceByServiceIdUseCase.Request("   ");

        // Action
        GetSpecialistServiceByServiceIdUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.FIELD_REQUIRED.getMessage());

        verify(service, never()).findById(anyString(), any(Context.class));
    }

    @Test
    void WHEN_execute_serviceNotFound_THEN_returnNotFoundError() {
        // Arrange
        GetSpecialistServiceByServiceIdUseCase.Request request = new GetSpecialistServiceByServiceIdUseCase.Request(SERVICE_ID);

        when(service.findById(eq(SERVICE_ID), any(Context.class)))
                .thenAnswer(invocation -> {
                    Context context = invocation.getArgument(1);
                    context.setError(ErrorCode.SPECIALIST_SERVICE_NOT_FOUND);
                    return null;
                });

        // Action
        GetSpecialistServiceByServiceIdUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.SPECIALIST_SERVICE_NOT_FOUND.getMessage());
        assertThat(response.service()).isNull();

        verify(service).findById(eq(SERVICE_ID), any(Context.class));
    }

    @Test
    void WHEN_execute_validRequest_THEN_returnCorrectServiceData() {
        // Arrange
        GetSpecialistServiceByServiceIdUseCase.Request request = new GetSpecialistServiceByServiceIdUseCase.Request(SERVICE_ID);
        SpecialistService specialistService = createSpecialistService();

        when(service.findById(eq(SERVICE_ID), any(Context.class))).thenReturn(specialistService);

        // Action
        GetSpecialistServiceByServiceIdUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.service().getTitle()).isEqualTo("Java Development");
        assertThat(response.service().getPrice()).isEqualTo(150);
        assertThat(response.service().getCurrency()).isEqualTo("USD");
    }

    private SpecialistService createSpecialistService() {
        return new SpecialistService(SERVICE_ID, PROFILE_ID, "Java Development", "Backend dev", 150, "USD", true);
    }
}

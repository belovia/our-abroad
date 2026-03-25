package ru.belov.ourabroad.api.usecases.change.specialistservice.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.change.specialistservice.ChangeSpecialistServiceUseCase;
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
                ChangeSpecialistServiceUseCaseImpl.class,
                FieldValidator.class
        }
)
class ChangeSpecialistServiceUseCaseImplTest {

    @MockitoBean
    private SpecialistServiceService specialistServiceService;

    @Autowired
    private ChangeSpecialistServiceUseCaseImpl usecase;

    @Captor
    private ArgumentCaptor<SpecialistService> serviceCaptor;

    private static final String SERVICE_ID = "service-123";
    private static final String SPECIALIST_ID = "specialist-123";
    private static final String CURRENCY = "USD";
    private static final String TITLE = "Updated Title";
    private static final Integer PRICE = 200;
    private static final String DESCRIPTION = "Updated description";

    @Test
    void contextCreated() {
        assertNotNull(usecase);
        assertNotNull(specialistServiceService);
    }

    @Test
    void WHEN_execute_validRequest_THEN_updateServiceSuccessfully() {
        // Arrange
        ChangeSpecialistServiceUseCase.Request request = createValidRequest();
        SpecialistService existingService = createSpecialistService("Old Title", 100, "Old desc");

        when(specialistServiceService.findById(eq(SERVICE_ID), any(Context.class)))
                .thenReturn(existingService);
        doNothing().when(specialistServiceService).update(any(SpecialistService.class));

        // Action
        ChangeSpecialistServiceUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isTrue();
        assertThat(response.serviceId()).isEqualTo(SERVICE_ID);
        assertThat(response.message()).isEqualTo(ErrorCode.SUCCESS.getMessage());

        verify(specialistServiceService).findById(eq(SERVICE_ID), any(Context.class));
        verify(specialistServiceService).update(serviceCaptor.capture());

        SpecialistService updated = serviceCaptor.getValue();
        assertThat(updated.getTitle()).isEqualTo(TITLE);
        assertThat(updated.getPrice()).isEqualTo(PRICE);
        assertThat(updated.getDescription()).isEqualTo(DESCRIPTION);
    }

    @Test
    void WHEN_execute_nullServiceId_THEN_returnValidationError() {
        // Arrange
        ChangeSpecialistServiceUseCase.Request request = new ChangeSpecialistServiceUseCase.Request(
                null, TITLE, PRICE, DESCRIPTION
        );

        // Action
        ChangeSpecialistServiceUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.serviceId()).isNull();
        assertThat(response.message()).isEqualTo(ErrorCode.FIELD_REQUIRED.getMessage());

        verify(specialistServiceService, never()).findById(anyString(), any(Context.class));
        verify(specialistServiceService, never()).update(any(SpecialistService.class));
    }

    @Test
    void WHEN_execute_nullTitle_THEN_returnValidationError() {
        // Arrange
        ChangeSpecialistServiceUseCase.Request request = new ChangeSpecialistServiceUseCase.Request(
                SERVICE_ID, null, PRICE, DESCRIPTION
        );

        // Action
        ChangeSpecialistServiceUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.serviceId()).isEqualTo(SERVICE_ID);
        assertThat(response.message()).isEqualTo(ErrorCode.FIELD_REQUIRED.getMessage());

        verify(specialistServiceService, never()).findById(anyString(), any(Context.class));
        verify(specialistServiceService, never()).update(any(SpecialistService.class));
    }

    @Test
    void WHEN_execute_nullPrice_THEN_returnValidationError() {
        // Arrange
        ChangeSpecialistServiceUseCase.Request request = new ChangeSpecialistServiceUseCase.Request(
                SERVICE_ID, TITLE, null, DESCRIPTION
        );

        // Action
        ChangeSpecialistServiceUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.serviceId()).isEqualTo(SERVICE_ID);
        assertThat(response.message()).isEqualTo(ErrorCode.FIELD_REQUIRED.getMessage());
    }

    @Test
    void WHEN_execute_nullDescription_THEN_returnValidationError() {
        // Arrange
        ChangeSpecialistServiceUseCase.Request request = new ChangeSpecialistServiceUseCase.Request(
                SERVICE_ID, TITLE, PRICE, null
        );

        // Action
        ChangeSpecialistServiceUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.serviceId()).isEqualTo(SERVICE_ID);
        assertThat(response.message()).isEqualTo(ErrorCode.FIELD_REQUIRED.getMessage());
    }

    @Test
    void WHEN_execute_serviceNotFound_THEN_returnNotFoundError() {
        // Arrange
        ChangeSpecialistServiceUseCase.Request request = createValidRequest();

        when(specialistServiceService.findById(eq(SERVICE_ID), any(Context.class)))
                .thenAnswer(invocation -> {
                    Context context = invocation.getArgument(1);
                    context.setError(ErrorCode.SPECIALIST_SERVICE_NOT_FOUND);
                    return null;
                });

        // Action
        ChangeSpecialistServiceUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.serviceId()).isEqualTo(SERVICE_ID);
        assertThat(response.message()).isEqualTo(ErrorCode.SPECIALIST_SERVICE_NOT_FOUND.getMessage());

        verify(specialistServiceService).findById(eq(SERVICE_ID), any(Context.class));
        verify(specialistServiceService, never()).update(any(SpecialistService.class));
    }

    @Test
    void WHEN_execute_invalidPrice_negative_THEN_returnPriceError() {
        // Arrange
        ChangeSpecialistServiceUseCase.Request request = new ChangeSpecialistServiceUseCase.Request(
                SERVICE_ID, TITLE, -50, DESCRIPTION
        );
        SpecialistService existingService = createSpecialistService(TITLE, 100, DESCRIPTION);

        when(specialistServiceService.findById(eq(SERVICE_ID), any(Context.class)))
                .thenReturn(existingService);

        // Action
        ChangeSpecialistServiceUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.serviceId()).isEqualTo(SERVICE_ID);
        assertThat(response.message()).isEqualTo(ErrorCode.PRICE_MUST_BE_BIGGER_THAN_ZERO.getMessage());

        verify(specialistServiceService).findById(eq(SERVICE_ID), any(Context.class));
        verify(specialistServiceService, never()).update(any(SpecialistService.class));
    }

    @Test
    void WHEN_execute_updateThrowsException_THEN_propagateException() {
        // Arrange
        ChangeSpecialistServiceUseCase.Request request = createValidRequest();
        SpecialistService existingService = createSpecialistService("Old", 100, "Old");

        when(specialistServiceService.findById(eq(SERVICE_ID), any(Context.class)))
                .thenReturn(existingService);
        doThrow(new RuntimeException("DB error")).when(specialistServiceService).update(any(SpecialistService.class));

        // Action + Assert
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> usecase.execute(request));
    }

    @Test
    void WHEN_execute_preserveOtherFields() {
        // Arrange
        ChangeSpecialistServiceUseCase.Request request = new ChangeSpecialistServiceUseCase.Request(
                SERVICE_ID, TITLE, PRICE, DESCRIPTION
        );
        SpecialistService original = new SpecialistService(
                SERVICE_ID, SPECIALIST_ID, "Old Title", "Old Desc", 150, CURRENCY, true
        );

        when(specialistServiceService.findById(eq(SERVICE_ID), any(Context.class)))
                .thenReturn(original);
        doNothing().when(specialistServiceService).update(any(SpecialistService.class));

        // Action
        usecase.execute(request);

        // Asserts
        verify(specialistServiceService).update(serviceCaptor.capture());
        SpecialistService updated = serviceCaptor.getValue();

        assertThat(updated.getId()).isEqualTo(SERVICE_ID);
        assertThat(updated.getTitle()).isEqualTo(TITLE);
        assertThat(updated.getPrice()).isEqualTo(PRICE);
        assertThat(updated.getDescription()).isEqualTo(DESCRIPTION);
    }

    @Test
    void WHEN_execute_validationFails_THEN_skipRetrieveAndUpdate() {
        // Arrange
        ChangeSpecialistServiceUseCase.Request request = new ChangeSpecialistServiceUseCase.Request(
                null, null, null, null
        );

        // Action
        ChangeSpecialistServiceUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();

        verify(specialistServiceService, never()).findById(anyString(), any(Context.class));
        verify(specialistServiceService, never()).update(any(SpecialistService.class));
    }

    private ChangeSpecialistServiceUseCase.Request createValidRequest() {
        return new ChangeSpecialistServiceUseCase.Request(SERVICE_ID, TITLE, PRICE, DESCRIPTION);
    }

    private SpecialistService createSpecialistService(String title, int price, String description) {
        return new SpecialistService(SERVICE_ID, SPECIALIST_ID, title, description, price, CURRENCY, true);
    }
}

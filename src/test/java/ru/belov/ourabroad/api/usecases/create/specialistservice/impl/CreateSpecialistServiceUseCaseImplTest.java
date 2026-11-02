package ru.belov.ourabroad.api.usecases.create.specialistservice.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.create.specialistservice.CreateSpecialistServiceUseCase;
import ru.belov.ourabroad.api.usecases.services.specialistservice.SpecialistServiceService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.web.validators.ErrorCode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                CreateSpecialistServiceUseCaseImpl.class
        }
)
class CreateSpecialistServiceUseCaseImplTest {

    @MockitoBean
    private SpecialistServiceService specialistServiceService;

    @Autowired
    private CreateSpecialistServiceUseCaseImpl usecase;

    @Captor
    private ArgumentCaptor<SpecialistService> serviceCaptor;

    private static final String PROFILE_ID = "profile-123";
    private static final String TITLE = "Java Development";
    private static final String DESCRIPTION = "Backend development with Spring Boot";
    private static final Integer PRICE = 150;

    @Test
    void contextCreated() {
        assertNotNull(usecase);
        assertNotNull(specialistServiceService);
    }

    @Test
    void WHEN_execute_validRequest_THEN_createServiceSuccessfully() {
        CreateSpecialistServiceUseCase.Request request = createValidRequest();
        doNothing().when(specialistServiceService).saveNew(any(SpecialistService.class), any(Context.class));

        CreateSpecialistServiceUseCase.Response response = usecase.execute(request);

        assertThat(response.success()).isTrue();
        assertThat(response.specialistProfileId()).isEqualTo(PROFILE_ID);
        assertThat(response.message()).isEqualTo(ErrorCode.SUCCESS.getMessage());

        verify(specialistServiceService).saveNew(serviceCaptor.capture(), any(Context.class));
        SpecialistService saved = serviceCaptor.getValue();
        assertThat(saved.getTitle()).isEqualTo(TITLE);
        assertThat(saved.getPrice()).isEqualTo(PRICE);
        assertThat(saved.getDescription()).isEqualTo(DESCRIPTION);
    }

    @Test
    void WHEN_execute_nullTitle_THEN_returnValidationError() {
        CreateSpecialistServiceUseCase.Request request = new CreateSpecialistServiceUseCase.Request(
                PROFILE_ID, null, DESCRIPTION, PRICE, "USD"
        );

        CreateSpecialistServiceUseCase.Response response = usecase.execute(request);

        assertThat(response.success()).isFalse();
        assertThat(response.specialistProfileId()).isEqualTo(PROFILE_ID);
        assertThat(response.message()).isEqualTo(ErrorCode.VALIDATION_ERROR.getMessage());

        verify(specialistServiceService, never()).saveNew(any(SpecialistService.class), any(Context.class));
    }

    @Test
    void WHEN_execute_blankTitle_THEN_returnValidationError() {
        CreateSpecialistServiceUseCase.Request request = new CreateSpecialistServiceUseCase.Request(
                PROFILE_ID, "   ", DESCRIPTION, PRICE, "USD"
        );

        CreateSpecialistServiceUseCase.Response response = usecase.execute(request);

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.VALIDATION_ERROR.getMessage());

        verify(specialistServiceService, never()).saveNew(any(SpecialistService.class), any(Context.class));
    }

    @Test
    void WHEN_execute_nullPrice_THEN_returnValidationError() {
        CreateSpecialistServiceUseCase.Request request = new CreateSpecialistServiceUseCase.Request(
                PROFILE_ID, TITLE, DESCRIPTION, null, "USD"
        );

        CreateSpecialistServiceUseCase.Response response = usecase.execute(request);

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.VALIDATION_ERROR.getMessage());

        verify(specialistServiceService, never()).saveNew(any(SpecialistService.class), any(Context.class));
    }

    @Test
    void WHEN_execute_negativePrice_THEN_returnValidationError() {
        CreateSpecialistServiceUseCase.Request request = new CreateSpecialistServiceUseCase.Request(
                PROFILE_ID, TITLE, DESCRIPTION, -10, "USD"
        );

        CreateSpecialistServiceUseCase.Response response = usecase.execute(request);

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.VALIDATION_ERROR.getMessage());

        verify(specialistServiceService, never()).saveNew(any(SpecialistService.class), any(Context.class));
    }

    @Test
    void WHEN_execute_zeroPriceIsValid_THEN_createServiceSuccessfully() {
        CreateSpecialistServiceUseCase.Request request = new CreateSpecialistServiceUseCase.Request(
                PROFILE_ID, TITLE, DESCRIPTION, 0, "USD"
        );
        doNothing().when(specialistServiceService).saveNew(any(SpecialistService.class), any(Context.class));

        CreateSpecialistServiceUseCase.Response response = usecase.execute(request);

        assertThat(response.success()).isTrue();
        verify(specialistServiceService).saveNew(any(SpecialistService.class), any(Context.class));
    }

    @Test
    void WHEN_execute_saveThrowsException_THEN_propagateException() {
        CreateSpecialistServiceUseCase.Request request = createValidRequest();
        doThrow(new RuntimeException("DB error")).when(specialistServiceService)
                .saveNew(any(SpecialistService.class), any(Context.class));

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> usecase.execute(request));
    }

    @Test
    void WHEN_execute_validRequest_THEN_currencyIsUsd() {
        CreateSpecialistServiceUseCase.Request request = createValidRequest();
        doNothing().when(specialistServiceService).saveNew(any(SpecialistService.class), any(Context.class));

        usecase.execute(request);

        verify(specialistServiceService).saveNew(serviceCaptor.capture(), any(Context.class));
        assertThat(serviceCaptor.getValue().getCurrency()).isEqualTo("USD");
    }

    private CreateSpecialistServiceUseCase.Request createValidRequest() {
        return new CreateSpecialistServiceUseCase.Request(PROFILE_ID, TITLE, DESCRIPTION, PRICE, "USD");
    }
}

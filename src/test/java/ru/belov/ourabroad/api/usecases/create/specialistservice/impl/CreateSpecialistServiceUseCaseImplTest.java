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
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.poi.storage.SpecialistServiceRepository;
import ru.belov.ourabroad.web.validators.ErrorCode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                CreateSpecialistServiceUseCaseImpl.class
        }
)
class CreateSpecialistServiceUseCaseImplTest {

    @MockitoBean
    private SpecialistServiceRepository repository;

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
        assertNotNull(repository);
    }

    @Test
    void WHEN_execute_validRequest_THEN_createServiceSuccessfully() {
        // Arrange
        CreateSpecialistServiceUseCase.Request request = createValidRequest();
        doNothing().when(repository).save(any(SpecialistService.class));

        // Action
        CreateSpecialistServiceUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isTrue();
        assertThat(response.specialistProfileId()).isEqualTo(PROFILE_ID);
        assertThat(response.message()).isEqualTo(ErrorCode.SUCCESS.getMessage());

        verify(repository).save(serviceCaptor.capture());
        SpecialistService saved = serviceCaptor.getValue();
        assertThat(saved.getTitle()).isEqualTo(TITLE);
        assertThat(saved.getPrice()).isEqualTo(PRICE);
        assertThat(saved.getDescription()).isEqualTo(DESCRIPTION);
    }

    @Test
    void WHEN_execute_nullTitle_THEN_returnValidationError() {
        // Arrange
        CreateSpecialistServiceUseCase.Request request = new CreateSpecialistServiceUseCase.Request(
                PROFILE_ID, null, DESCRIPTION, PRICE, "USD"
        );

        // Action
        CreateSpecialistServiceUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.specialistProfileId()).isEqualTo(PROFILE_ID);
        assertThat(response.message()).isEqualTo(ErrorCode.VALIDATION_ERROR.getMessage());

        verify(repository, never()).save(any(SpecialistService.class));
    }

    @Test
    void WHEN_execute_blankTitle_THEN_returnValidationError() {
        // Arrange
        CreateSpecialistServiceUseCase.Request request = new CreateSpecialistServiceUseCase.Request(
                PROFILE_ID, "   ", DESCRIPTION, PRICE, "USD"
        );

        // Action
        CreateSpecialistServiceUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.VALIDATION_ERROR.getMessage());

        verify(repository, never()).save(any(SpecialistService.class));
    }

    @Test
    void WHEN_execute_nullPrice_THEN_returnValidationError() {
        // Arrange
        CreateSpecialistServiceUseCase.Request request = new CreateSpecialistServiceUseCase.Request(
                PROFILE_ID, TITLE, DESCRIPTION, null, "USD"
        );

        // Action
        CreateSpecialistServiceUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.VALIDATION_ERROR.getMessage());

        verify(repository, never()).save(any(SpecialistService.class));
    }

    @Test
    void WHEN_execute_negativePrice_THEN_returnValidationError() {
        // Arrange
        CreateSpecialistServiceUseCase.Request request = new CreateSpecialistServiceUseCase.Request(
                PROFILE_ID, TITLE, DESCRIPTION, -10, "USD"
        );

        // Action
        CreateSpecialistServiceUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.VALIDATION_ERROR.getMessage());

        verify(repository, never()).save(any(SpecialistService.class));
    }

    @Test
    void WHEN_execute_zeroPriceIsValid_THEN_createServiceSuccessfully() {
        // Arrange
        CreateSpecialistServiceUseCase.Request request = new CreateSpecialistServiceUseCase.Request(
                PROFILE_ID, TITLE, DESCRIPTION, 0, "USD"
        );
        doNothing().when(repository).save(any(SpecialistService.class));

        // Action
        CreateSpecialistServiceUseCase.Response response = usecase.execute(request);

        // Asserts
        assertThat(response.success()).isTrue();
        verify(repository).save(any(SpecialistService.class));
    }

    @Test
    void WHEN_execute_saveThrowsException_THEN_propagateException() {
        // Arrange
        CreateSpecialistServiceUseCase.Request request = createValidRequest();
        doThrow(new RuntimeException("DB error")).when(repository).save(any(SpecialistService.class));

        // Action + Assert
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> usecase.execute(request));
    }

    @Test
    void WHEN_execute_validRequest_THEN_currencyIsUsd() {
        // Arrange
        CreateSpecialistServiceUseCase.Request request = createValidRequest();
        doNothing().when(repository).save(any(SpecialistService.class));

        // Action
        usecase.execute(request);

        // Asserts
        verify(repository).save(serviceCaptor.capture());
        assertThat(serviceCaptor.getValue().getCurrency()).isEqualTo("USD");
    }

    private CreateSpecialistServiceUseCase.Request createValidRequest() {
        return new CreateSpecialistServiceUseCase.Request(PROFILE_ID, TITLE, DESCRIPTION, PRICE, "USD");
    }
}

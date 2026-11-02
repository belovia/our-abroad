package ru.belov.ourabroad.api.usecases.create.booking.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.create.booking.CreateBookingUseCase;
import ru.belov.ourabroad.api.usecases.services.booking.BookingService;
import ru.belov.ourabroad.api.usecases.services.specialistprofile.SpecialistProfileService;
import ru.belov.ourabroad.config.security.CurrentUserProvider;
import ru.belov.ourabroad.core.domain.Booking;
import ru.belov.ourabroad.core.domain.BookingFactory;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistProfile;
import ru.belov.ourabroad.core.enums.BookingStatus;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.FieldValidator;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        CreateBookingUseCaseImpl.class,
        FieldValidator.class
})
class CreateBookingUseCaseImplTest {

    private static final String USER_ID = "user-1";
    private static final String SPECIALIST_ID = "spec-profile-1";
    private static final String SERVICE_ID = "service-1";
    private static final LocalDateTime START = LocalDateTime.of(2026, 5, 1, 10, 0);

    @MockitoBean
    private SpecialistProfileService specialistProfileService;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private CurrentUserProvider currentUserProvider;

    @Autowired
    private CreateBookingUseCaseImpl useCase;

    @BeforeEach
    void stubUser() {
        when(currentUserProvider.requiredUserId()).thenReturn(USER_ID);
    }

    @Test
    void contextCreated() {
        assertNotNull(useCase);
    }

    @Test
    void WHEN_execute_valid_THEN_createBookingSuccessfully() {
        SpecialistProfile profile = SpecialistProfile.builder()
                .id(SPECIALIST_ID)
                .userId("spec-user-1")
                .description("d")
                .active(true)
                .rating(0)
                .reviewsCount(0)
                .build();
        when(specialistProfileService.findById(eq(SPECIALIST_ID), any(Context.class))).thenReturn(profile);

        Booking created = BookingFactory.create(USER_ID, SPECIALIST_ID, SERVICE_ID, START);
        when(bookingService.createBooking(eq(USER_ID), eq(SPECIALIST_ID), eq(SERVICE_ID), eq(START), any(Context.class)))
                .thenReturn(created);

        CreateBookingUseCase.Request request = new CreateBookingUseCase.Request(
                SPECIALIST_ID, SERVICE_ID, START
        );
        CreateBookingUseCase.Response response = useCase.execute(request);

        assertThat(response.success()).isTrue();
        assertThat(response.message()).isEqualTo(ErrorCode.SUCCESS.getMessage());
        assertThat(response.bookingId()).isEqualTo(created.getId());
        assertThat(response.userId()).isEqualTo(USER_ID);
        assertThat(response.specialistId()).isEqualTo(SPECIALIST_ID);
        assertThat(response.serviceId()).isEqualTo(SERVICE_ID);
        assertThat(response.startTime()).isEqualTo(START);
        assertThat(response.status()).isEqualTo(BookingStatus.PENDING);
        assertThat(response.createdAt()).isEqualTo(created.getCreatedAt());

        verify(bookingService).createBooking(eq(USER_ID), eq(SPECIALIST_ID), eq(SERVICE_ID), eq(START), any(Context.class));
    }

    @Test
    void WHEN_specialistNotFound_THEN_specialistNotFoundError() {
        when(specialistProfileService.findById(eq(SPECIALIST_ID), any(Context.class))).thenAnswer(invocation -> {
            Context ctx = invocation.getArgument(1);
            ctx.setError(ErrorCode.SPECIALIST_PROFILE_NOT_FOUND);
            return null;
        });

        CreateBookingUseCase.Response response = useCase.execute(
                new CreateBookingUseCase.Request(SPECIALIST_ID, SERVICE_ID, START)
        );

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.SPECIALIST_NOT_FOUND.getMessage());
        verify(bookingService, never()).createBooking(any(), any(), any(), any(), any());
    }

    @Test
    void WHEN_specialistInactive_THEN_specialistNotFoundError() {
        SpecialistProfile profile = SpecialistProfile.builder()
                .id(SPECIALIST_ID)
                .userId("spec-user-1")
                .description("d")
                .active(false)
                .rating(0)
                .reviewsCount(0)
                .build();
        when(specialistProfileService.findById(eq(SPECIALIST_ID), any(Context.class))).thenReturn(profile);

        CreateBookingUseCase.Response response = useCase.execute(
                new CreateBookingUseCase.Request(SPECIALIST_ID, SERVICE_ID, START)
        );

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.SPECIALIST_NOT_FOUND.getMessage());
        verify(bookingService, never()).createBooking(any(), any(), any(), any(), any());
    }

    @Test
    void WHEN_serviceNotAvailable_THEN_serviceNotAvailableError() {
        SpecialistProfile profile = SpecialistProfile.builder()
                .id(SPECIALIST_ID)
                .userId("spec-user-1")
                .description("d")
                .active(true)
                .rating(0)
                .reviewsCount(0)
                .build();
        when(specialistProfileService.findById(eq(SPECIALIST_ID), any(Context.class))).thenReturn(profile);

        when(bookingService.createBooking(eq(USER_ID), eq(SPECIALIST_ID), eq(SERVICE_ID), eq(START), any(Context.class)))
                .thenAnswer(invocation -> {
                    Context ctx = invocation.getArgument(4);
                    ctx.setError(ErrorCode.SERVICE_NOT_AVAILABLE);
                    return null;
                });

        CreateBookingUseCase.Response response = useCase.execute(
                new CreateBookingUseCase.Request(SPECIALIST_ID, SERVICE_ID, START)
        );

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo(ErrorCode.SERVICE_NOT_AVAILABLE.getMessage());
    }
}

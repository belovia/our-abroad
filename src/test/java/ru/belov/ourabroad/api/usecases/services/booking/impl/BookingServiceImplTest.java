package ru.belov.ourabroad.api.usecases.services.booking.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.services.booking.BookingService;
import ru.belov.ourabroad.api.usecases.services.specialistprofile.SpecialistProfileService;
import ru.belov.ourabroad.core.domain.Booking;
import ru.belov.ourabroad.core.domain.BookingFactory;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistProfile;
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.core.enums.BookingStatus;
import ru.belov.ourabroad.poi.storage.BookingRepository;
import ru.belov.ourabroad.poi.storage.SpecialistServiceRepository;
import ru.belov.ourabroad.web.validators.ErrorCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = BookingServiceImpl.class)
class BookingServiceImplTest {

    private static final String USER_ID = "u1";
    private static final String SPECIALIST_ID = "sp1";
    private static final String SERVICE_ID = "srv1";
    private static final LocalDateTime START = LocalDateTime.of(2026, 6, 1, 12, 0);

    @MockitoBean
    private BookingRepository bookingRepository;

    @MockitoBean
    private SpecialistServiceRepository specialistServiceRepository;

    @MockitoBean
    private SpecialistProfileService specialistProfileService;

    @Autowired
    private BookingService bookingService;

    @Test
    void contextCreated() {
        assertNotNull(bookingService);
    }

    @Test
    void WHEN_createBooking_serviceInactive_THEN_serviceNotAvailable() {
        SpecialistService service = SpecialistServiceFactoryInactive.service(SPECIALIST_ID, false);
        when(specialistServiceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        Context context = new Context();
        Booking booking = bookingService.createBooking(USER_ID, SPECIALIST_ID, SERVICE_ID, START, context);

        assertThat(booking).isNull();
        assertThat(context.isSuccess()).isFalse();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.SERVICE_NOT_AVAILABLE);
    }

    @Test
    void WHEN_createBooking_wrongSpecialist_THEN_serviceNotAvailable() {
        SpecialistService service = SpecialistServiceFactoryInactive.service("other-spec", true);
        when(specialistServiceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        Context context = new Context();
        Booking booking = bookingService.createBooking(USER_ID, SPECIALIST_ID, SERVICE_ID, START, context);

        assertThat(booking).isNull();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.SERVICE_NOT_AVAILABLE);
    }

    @Test
    void WHEN_createBooking_valid_THEN_pendingAndSaved() {
        SpecialistService service = SpecialistServiceFactoryInactive.service(SPECIALIST_ID, true);
        when(specialistServiceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        Context context = new Context();
        Booking booking = bookingService.createBooking(USER_ID, SPECIALIST_ID, SERVICE_ID, START, context);

        assertThat(context.isSuccess()).isTrue();
        assertThat(booking).isNotNull();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
        assertThat(booking.getUserId()).isEqualTo(USER_ID);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void WHEN_cancelBooking_alreadyCancelled_THEN_invalidState() {
        Booking existing = BookingFactory.create(USER_ID, SPECIALIST_ID, SERVICE_ID, START);
        existing.setStatus(BookingStatus.CANCELLED);
        when(bookingRepository.findById("b1")).thenReturn(Optional.of(existing));

        Context context = new Context();
        bookingService.cancelBooking("b1", USER_ID, context);

        assertThat(context.isSuccess()).isFalse();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.INVALID_BOOKING_STATE);
    }

    @Test
    void WHEN_cancelBooking_valid_THEN_cancelled() {
        Booking existing = BookingFactory.create(USER_ID, SPECIALIST_ID, SERVICE_ID, START);
        when(bookingRepository.findById("b1")).thenReturn(Optional.of(existing));
        when(bookingRepository.updateStatus("b1", BookingStatus.CANCELLED)).thenReturn(true);

        Context context = new Context();
        bookingService.cancelBooking("b1", USER_ID, context);

        assertThat(context.isSuccess()).isTrue();
        verify(bookingRepository).updateStatus("b1", BookingStatus.CANCELLED);
    }

    @Test
    void WHEN_confirmBooking_wrongOwner_THEN_accessDenied() {
        Booking existing = BookingFactory.create(USER_ID, SPECIALIST_ID, SERVICE_ID, START);
        when(bookingRepository.findById("b1")).thenReturn(Optional.of(existing));

        SpecialistProfile profile = SpecialistProfile.builder()
                .id(SPECIALIST_ID)
                .userId("real-owner")
                .description("d")
                .active(true)
                .rating(0)
                .reviewsCount(0)
                .build();
        when(specialistProfileService.findById(eq(SPECIALIST_ID), any(Context.class))).thenReturn(profile);

        Context context = new Context();
        bookingService.confirmBooking("b1", "intruder", context);

        assertThat(context.isSuccess()).isFalse();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.ACCESS_DENIED);
    }

    @Test
    void WHEN_confirmBooking_valid_THEN_confirmed() {
        Booking existing = BookingFactory.create(USER_ID, SPECIALIST_ID, SERVICE_ID, START);
        when(bookingRepository.findById("b1")).thenReturn(Optional.of(existing));

        SpecialistProfile profile = SpecialistProfile.builder()
                .id(SPECIALIST_ID)
                .userId("owner")
                .description("d")
                .active(true)
                .rating(0)
                .reviewsCount(0)
                .build();
        when(specialistProfileService.findById(eq(SPECIALIST_ID), any(Context.class))).thenReturn(profile);
        when(bookingRepository.updateStatus("b1", BookingStatus.CONFIRMED)).thenReturn(true);

        Context context = new Context();
        bookingService.confirmBooking("b1", "owner", context);

        assertThat(context.isSuccess()).isTrue();
        verify(bookingRepository).updateStatus("b1", BookingStatus.CONFIRMED);
    }

    @Test
    void WHEN_getUserBookings_THEN_returnsFromRepository() {
        Booking b = BookingFactory.create(USER_ID, SPECIALIST_ID, SERVICE_ID, START);
        when(bookingRepository.findByUserId(USER_ID)).thenReturn(List.of(b));

        Context context = new Context();
        List<Booking> list = bookingService.getUserBookings(USER_ID, context);

        assertThat(context.isSuccess()).isTrue();
        assertThat(list).hasSize(1);
    }

    private static final class SpecialistServiceFactoryInactive {
        static SpecialistService service(String specialistId, boolean active) {
            return new SpecialistService(
                    SERVICE_ID,
                    specialistId,
                    "t",
                    "d",
                    100,
                    "USD",
                    active
            );
        }
    }
}

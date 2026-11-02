package ru.belov.ourabroad.api.usecases.services.booking.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.belov.ourabroad.api.usecases.services.booking.BookingService;
import ru.belov.ourabroad.api.usecases.services.specialistprofile.SpecialistProfileService;
import ru.belov.ourabroad.api.usecases.services.specialistservice.SpecialistServiceService;
import ru.belov.ourabroad.core.domain.Booking;
import ru.belov.ourabroad.core.domain.BookingFactory;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistProfile;
import ru.belov.ourabroad.core.enums.BookingStatus;
import ru.belov.ourabroad.poi.storage.BookingRepository;
import ru.belov.ourabroad.web.validators.ErrorCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final SpecialistServiceService specialistServiceService;
    private final SpecialistProfileService specialistProfileService;

    @Override
    public Booking createBooking(
            String userId,
            String specialistId,
            String serviceId,
            LocalDateTime startTime,
            Context context
    ) {
        if (!context.isSuccess()) {
            return null;
        }
        if (!StringUtils.hasText(userId)
                || !StringUtils.hasText(specialistId)
                || !StringUtils.hasText(serviceId)
                || startTime == null) {
            context.setError(ErrorCode.REQUEST_VALIDATION_ERROR);
            return null;
        }

        if (specialistServiceService.requireActiveServiceForSpecialist(serviceId, specialistId, context) == null) {
            log.warn(
                    "[userId: {}] Service not available for specialist: serviceId={}, specialistId={}",
                    userId,
                    serviceId,
                    specialistId
            );
            return null;
        }

        Booking booking = BookingFactory.create(userId, specialistId, serviceId, startTime);
        bookingRepository.save(booking);
        log.info("[bookingId: {}] Booking created for user {}", booking.getId(), userId);
        return booking;
    }

    @Override
    public void cancelBooking(String bookingId, String actingUserId, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        Booking booking = loadBookingOrSetError(bookingId, context);
        if (booking == null) {
            return;
        }
        if (!actingUserId.equals(booking.getUserId())) {
            context.setError(ErrorCode.ACCESS_DENIED);
            return;
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            context.setError(ErrorCode.INVALID_BOOKING_STATE);
            return;
        }
        boolean updated = bookingRepository.updateStatus(bookingId, BookingStatus.CANCELLED);
        if (!updated) {
            context.setError(ErrorCode.BOOKING_NOT_FOUND);
        }
    }

    @Override
    public void confirmBooking(String bookingId, String actingSpecialistUserId, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        Booking booking = loadBookingOrSetError(bookingId, context);
        if (booking == null) {
            return;
        }

        SpecialistProfile profile = specialistProfileService.findById(booking.getSpecialistId(), context);
        if (!context.isSuccess() || profile == null) {
            return;
        }
        if (!actingSpecialistUserId.equals(profile.getUserId())) {
            context.setError(ErrorCode.ACCESS_DENIED);
            return;
        }
        if (booking.getStatus() != BookingStatus.PENDING) {
            context.setError(ErrorCode.INVALID_BOOKING_STATE);
            return;
        }

        boolean updated = bookingRepository.updateStatus(bookingId, BookingStatus.CONFIRMED);
        if (!updated) {
            context.setError(ErrorCode.BOOKING_NOT_FOUND);
        }
    }

    @Override
    public List<Booking> getUserBookings(String userId, Context context) {
        if (!context.isSuccess()) {
            return List.of();
        }
        if (!StringUtils.hasText(userId)) {
            context.setError(ErrorCode.USER_ID_REQUIRED);
            return List.of();
        }
        return bookingRepository.findByUserId(userId);
    }

    @Override
    public List<Booking> getSpecialistBookings(String specialistProfileId, Context context) {
        if (!context.isSuccess()) {
            return List.of();
        }
        if (!StringUtils.hasText(specialistProfileId)) {
            context.setError(ErrorCode.FIELD_REQUIRED);
            return List.of();
        }
        return bookingRepository.findBySpecialistId(specialistProfileId);
    }

    @Override
    public Booking findById(String bookingId, Context context) {
        if (!context.isSuccess()) {
            return null;
        }
        return loadBookingOrSetError(bookingId, context);
    }

    private Booking loadBookingOrSetError(String bookingId, Context context) {
        Optional<Booking> found = bookingRepository.findById(bookingId);
        if (found.isEmpty()) {
            log.warn("[bookingId: {}] Booking not found", bookingId);
            context.setError(ErrorCode.BOOKING_NOT_FOUND);
            return null;
        }
        return found.get();
    }
}

package ru.belov.ourabroad.api.usecases.services.booking;

import ru.belov.ourabroad.core.domain.Booking;
import ru.belov.ourabroad.core.domain.Context;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    Booking createBooking(
            String userId,
            String specialistId,
            String serviceId,
            LocalDateTime startTime,
            Context context
    );

    void cancelBooking(String bookingId, String actingUserId, Context context);

    void confirmBooking(String bookingId, String actingSpecialistUserId, Context context);

    List<Booking> getUserBookings(String userId, Context context);

    List<Booking> getSpecialistBookings(String specialistProfileId, Context context);

    Booking findById(String bookingId, Context context);
}

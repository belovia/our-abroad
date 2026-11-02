package ru.belov.ourabroad.poi.storage;

import ru.belov.ourabroad.core.domain.Booking;
import ru.belov.ourabroad.core.enums.BookingStatus;

import java.util.List;
import java.util.Optional;

public interface BookingRepository {

    void save(Booking booking);

    Optional<Booking> findById(String id);

    List<Booking> findByUserId(String userId);

    List<Booking> findBySpecialistId(String specialistId);

    boolean updateStatus(String bookingId, BookingStatus status);
}

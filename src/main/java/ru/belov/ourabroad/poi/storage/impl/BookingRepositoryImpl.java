package ru.belov.ourabroad.poi.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.belov.ourabroad.core.domain.Booking;
import ru.belov.ourabroad.core.enums.BookingStatus;
import ru.belov.ourabroad.poi.storage.BookingRepository;
import ru.belov.ourabroad.poi.storage.mappers.BookingRowMapper;
import ru.belov.ourabroad.poi.storage.sql.BookingSql;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BookingRepositoryImpl implements BookingRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final BookingRowMapper rowMapper;

    @Override
    public void save(Booking booking) {
        jdbc.update(
                BookingSql.UPSERT,
                Map.of(
                        "id", booking.getId(),
                        "userId", booking.getUserId(),
                        "specialistId", booking.getSpecialistId(),
                        "serviceId", booking.getServiceId(),
                        "startTime", booking.getStartTime(),
                        "status", booking.getStatus().name(),
                        "createdAt", booking.getCreatedAt()
                )
        );
    }

    @Override
    public Optional<Booking> findById(String id) {
        return jdbc.query(
                BookingSql.FIND_BY_ID,
                Map.of("id", id),
                rowMapper
        ).stream().findFirst();
    }

    @Override
    public List<Booking> findByUserId(String userId) {
        return jdbc.query(
                BookingSql.FIND_BY_USER_ID,
                Map.of("userId", userId),
                rowMapper
        );
    }

    @Override
    public List<Booking> findBySpecialistId(String specialistId) {
        return jdbc.query(
                BookingSql.FIND_BY_SPECIALIST_ID,
                Map.of("specialistId", specialistId),
                rowMapper
        );
    }

    @Override
    public boolean updateStatus(String bookingId, BookingStatus status) {
        int updated = jdbc.update(
                BookingSql.UPDATE_STATUS,
                Map.of(
                        "id", bookingId,
                        "status", status.name()
                )
        );
        return updated > 0;
    }
}

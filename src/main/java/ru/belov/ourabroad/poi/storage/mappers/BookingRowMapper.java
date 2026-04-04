package ru.belov.ourabroad.poi.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.core.domain.Booking;
import ru.belov.ourabroad.core.domain.BookingFactory;
import ru.belov.ourabroad.core.enums.BookingStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class BookingRowMapper implements RowMapper<Booking> {

    @Override
    public Booking mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp startTs = rs.getTimestamp("start_time");
        Timestamp createdTs = rs.getTimestamp("created_at");
        return BookingFactory.fromDb(
                rs.getString("id"),
                rs.getString("user_id"),
                rs.getString("specialist_id"),
                rs.getString("service_id"),
                startTs != null ? startTs.toLocalDateTime() : null,
                BookingStatus.valueOf(rs.getString("status")),
                createdTs != null ? createdTs.toLocalDateTime() : null
        );
    }
}

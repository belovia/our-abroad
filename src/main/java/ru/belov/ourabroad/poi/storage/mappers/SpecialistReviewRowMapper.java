package ru.belov.ourabroad.poi.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.core.domain.SpecialistReview;
import ru.belov.ourabroad.core.domain.SpecialistReviewFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class SpecialistReviewRowMapper implements RowMapper<SpecialistReview> {

    @Override
    public SpecialistReview mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp createdTs = rs.getTimestamp("created_at");
        return SpecialistReviewFactory.fromDb(
                rs.getString("id"),
                rs.getString("booking_id"),
                rs.getString("author_id"),
                rs.getString("specialist_id"),
                rs.getInt("score"),
                rs.getString("text"),
                createdTs != null ? createdTs.toLocalDateTime() : null
        );
    }
}

package ru.belov.ourabroad.poi.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.belov.ourabroad.core.domain.SpecialistReview;
import ru.belov.ourabroad.poi.storage.SpecialistReviewRepository;
import ru.belov.ourabroad.poi.storage.mappers.SpecialistReviewRowMapper;
import ru.belov.ourabroad.poi.storage.sql.SpecialistReviewSql;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class SpecialistReviewRepositoryImpl implements SpecialistReviewRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final SpecialistReviewRowMapper rowMapper;

    @Override
    public void save(SpecialistReview review) {
        jdbc.update(
                SpecialistReviewSql.INSERT,
                Map.of(
                        "id", review.getId(),
                        "bookingId", review.getBookingId(),
                        "authorId", review.getAuthorId(),
                        "specialistId", review.getSpecialistId(),
                        "score", review.getScore(),
                        "text", review.getText() != null ? review.getText() : "",
                        "createdAt", review.getCreatedAt()
                )
        );
    }

    @Override
    public boolean existsByBookingId(String bookingId) {
        Boolean exists = jdbc.queryForObject(
                SpecialistReviewSql.EXISTS_BY_BOOKING_ID,
                Map.of("bookingId", bookingId),
                Boolean.class
        );
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public List<SpecialistReview> findBySpecialistId(String specialistId) {
        return jdbc.query(
                SpecialistReviewSql.FIND_BY_SPECIALIST_ID,
                Map.of("specialistId", specialistId),
                rowMapper
        );
    }

    @Override
    public ReviewStats averageScoreAndCount(String specialistId) {
        return jdbc.queryForObject(
                SpecialistReviewSql.AVERAGE_SCORE_AND_COUNT,
                Map.of("specialistId", specialistId),
                (rs, rowNum) -> new ReviewStats(rs.getDouble("rating"), rs.getInt("reviews_count"))
        );
    }
}

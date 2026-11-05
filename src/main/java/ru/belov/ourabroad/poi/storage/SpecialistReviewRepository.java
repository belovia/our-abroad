package ru.belov.ourabroad.poi.storage;

import ru.belov.ourabroad.core.domain.SpecialistReview;

import java.util.List;

public interface SpecialistReviewRepository {

    void save(SpecialistReview review);

    boolean existsByBookingId(String bookingId);

    List<SpecialistReview> findBySpecialistId(String specialistId);

    ReviewStats averageScoreAndCount(String specialistId);

    record ReviewStats(double rating, int reviewsCount) {
    }
}

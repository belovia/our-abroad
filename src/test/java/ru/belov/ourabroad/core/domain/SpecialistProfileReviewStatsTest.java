package ru.belov.ourabroad.core.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialistProfileReviewStatsTest {

    @Test
    void WHEN_firstReview_THEN_ratingBecomesScoreAndCountOne() {
        SpecialistProfile profile = profile(0.0, 0);

        profile.applyReviewStats(4.0, 1);

        assertThat(profile.getRating()).isEqualTo(4.0);
        assertThat(profile.getReviewsCount()).isEqualTo(1);
    }

    @Test
    void WHEN_secondReview_THEN_ratingIsAverage() {
        SpecialistProfile profile = profile(4.0, 1);

        profile.applyReviewStats(4.5, 2);

        assertThat(profile.getRating()).isEqualTo(4.5);
        assertThat(profile.getReviewsCount()).isEqualTo(2);
    }

    private static SpecialistProfile profile(double rating, int reviewsCount) {
        return SpecialistProfile.builder()
                .id("sp-1")
                .userId("user-1")
                .rating(rating)
                .reviewsCount(reviewsCount)
                .build();
    }
}

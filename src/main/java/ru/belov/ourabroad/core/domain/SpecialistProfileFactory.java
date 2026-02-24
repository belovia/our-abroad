package ru.belov.ourabroad.core.domain;

import java.util.UUID;

public final class SpecialistProfileFactory {

    private SpecialistProfileFactory() {
    }

    public static SpecialistProfile fromDb(
            String id,
            String userId,
            String description,
            boolean active,
            double rating,
            int reviewsCount
    ) {
        return SpecialistProfile.builder()
                .id(id)
                .userId(userId)
                .description(description)
                .active(active)
                .rating(rating)
                .reviewsCount(reviewsCount)
                .build();
    }

    public static SpecialistProfile create(
            String userId,
            String description
    ) {
        return SpecialistProfile.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .description(description)
                .active(true)
                .rating(0.0)
                .reviewsCount(0)
                .build();
    }

}
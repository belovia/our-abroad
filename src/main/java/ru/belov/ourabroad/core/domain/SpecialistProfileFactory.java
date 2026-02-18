package ru.belov.ourabroad.core.domain;

import ru.belov.ourabroad.core.domain.SpecialistProfile;

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
        return SpecialistProfile.create(
                id,
                userId,
                description,
                active,
                rating,
                reviewsCount
        );
    }
    public static SpecialistProfile create(String id, String description) {
        return SpecialistProfile.builder()
                .userId(id)
                .id(UUID.randomUUID().toString())
                .description(description)
                .rating(0.0)
                .active(true)
                .build();
    }

}
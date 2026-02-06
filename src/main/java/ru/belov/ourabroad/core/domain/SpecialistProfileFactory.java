package ru.belov.ourabroad.core.domain;

import ru.belov.ourabroad.core.domain.SpecialistProfile;

import java.util.UUID;

public final class SpecialistProfileFactory {

    private SpecialistProfileFactory() {
    }

    public static SpecialistProfile fromDb(
            String id,
            String userId,
            String category,
            String description,
            Integer priceFrom,
            Integer priceTo,
            boolean active,
            double rating,
            int reviewsCount
    ) {
        return SpecialistProfile.create(
                id,
                userId,
                category,
                description,
                priceFrom,
                priceTo,
                active,
                rating,
                reviewsCount
        );
    }

    public static SpecialistProfile newProfile(
            String id,
            String userId,
            String category
    ) {
        return SpecialistProfile.create(
                id,
                userId,
                category,
                null,
                null,
                null,
                true,
                0.0,
                0
        );
    }
}
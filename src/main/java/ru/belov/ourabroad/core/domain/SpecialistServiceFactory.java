package ru.belov.ourabroad.core.domain;

import java.util.Objects;

public class SpecialistServiceFactory {

    private SpecialistServiceFactory() {}

    public static SpecialistService fromDb(
            String id,
            String specialistId,
            String title,
            String description,
            Integer price,
            String currency,
            boolean active
    ) {
        return new SpecialistService(
                id,
                specialistId,
                title,
                description,
                price,
                currency,
                active
        );
    }

    public static SpecialistService create(
            String id,
            String specialistId,
            String title,
            String description,
            Integer price,
            String currency
    ) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(specialistId, "specialistId must not be null");
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(price, "price must not be null");
        Objects.requireNonNull(currency, "currency must not be null");

        if (price < 0) {
            throw new IllegalArgumentException("price must be positive");
        }

        return new SpecialistService(
                id,
                specialistId,
                title,
                description,
                price,
                currency,
                true // new services active by default
        );
    }
}

package ru.belov.ourabroad.core.domain;

import java.util.Objects;
import java.util.UUID;

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
            String specialistId,
            String title,
            String description,
            Integer price,
            String currency
    ) {
        Objects.requireNonNull(specialistId, "specialistId must not be null");
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(price, "price must not be null");
        Objects.requireNonNull(currency, "currency must not be null");

        String id = UUID.randomUUID().toString();

        return new SpecialistService(
                id,
                specialistId,
                title,
                description,
                price,
                currency,
                true
        );
    }
}

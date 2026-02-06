package ru.belov.ourabroad.core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class SpecialistProfile {

    private final String id;
    private final String userId;

    private String category;
    private String description;
    private Integer priceFrom;
    private Integer priceTo;
    private boolean active;
    private double rating;
    private int reviewsCount;

    private SpecialistProfile(String id, String userId) {
        this.id = id;
        this.userId = userId;
    }

    public static SpecialistProfile create(
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
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(userId, "userId must not be null");

        SpecialistProfile sp = new SpecialistProfile(id, userId);
        sp.category = category;
        sp.description = description;
        sp.priceFrom = priceFrom;
        sp.priceTo = priceTo;
        sp.active = active;
        sp.rating = rating;
        sp.reviewsCount = reviewsCount;
        return sp;
    }
}
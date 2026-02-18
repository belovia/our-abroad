package ru.belov.ourabroad.core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@Builder
public class SpecialistProfile {

    private final String id;
    private final String userId;

    private String description;
    private boolean active;
    private double rating;
    private int reviewsCount;
    private Set<SpecialistService> services;

    private SpecialistProfile(String id, String userId) {
        this.id = id;
        this.userId = userId;
    }

    public static SpecialistProfile create(
            String id,
            String userId,
            String description,
            boolean active,
            double rating,
            int reviewsCount
    ) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(userId);

        SpecialistProfile sp = new SpecialistProfile(id, userId);
        sp.description = description;
        sp.active = active;
        sp.rating = rating;
        sp.reviewsCount = reviewsCount;
        return sp;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
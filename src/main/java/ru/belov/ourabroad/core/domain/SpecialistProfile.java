package ru.belov.ourabroad.core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

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

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
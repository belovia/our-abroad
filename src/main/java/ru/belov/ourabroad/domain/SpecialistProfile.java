package ru.belov.ourabroad.domain;

import lombok.Getter;

@Getter
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


    public SpecialistProfile(String id, String userId) {
        this.id = id;
        this.userId = userId;
        this.active = true;
    }


    public void deactivate() {
        this.active = false;
    }
}
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
        this.rating = 0.0;
        this.reviewsCount = 0;
    }
    public void update(
            String category,
            String description,
            Integer priceFrom,
            Integer priceTo
    ) {
        this.category = category;
        this.description = description;
        this.priceFrom = priceFrom;
        this.priceTo = priceTo;
    }


    public void updateStats(double rating, int reviewsCount) {
        this.rating = rating;
        this.reviewsCount = reviewsCount;
    }


    public void deactivate() {
        this.active = false;
    }


    public void activate() {
        this.active = true;
    }
}
package ru.belov.ourabroad.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class SpecialistService {

    private final String id;
    private final String specialistId;

    private String title;
    private String description;
    private Integer price;
    private String currency;
    private boolean active;

    public SpecialistService(
            String id,
            String specialistId,
            String title,
            String description,
            Integer price,
            String currency,
            boolean active
    ) {
        this.id = id;
        this.specialistId = specialistId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.currency = currency;
        this.active = active;
    }

}
package ru.belov.ourabroad.web.dto.change;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ChangeSpecialistProfileRequest {

    private String description;
    private Boolean active;

    // полный список услуг после редактирования
    private Set<SpecialistServiceDto> services;
}

package ru.belov.ourabroad.api.usecases.create.specialistservice;

import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.web.dto.change.SpecialistServiceDto;

public interface CreateSpecialistServiceUseCase {
    SpecialistService create(
            String specialistProfileId,
            SpecialistServiceDto dto
    );
}

package ru.belov.ourabroad.api.usecases.change.specialistservice;

import ru.belov.ourabroad.web.dto.change.SpecialistServiceDto;

public interface ChangeSpecialistServiceUseCase {
    void change(String serviceId, SpecialistServiceDto dto);
}

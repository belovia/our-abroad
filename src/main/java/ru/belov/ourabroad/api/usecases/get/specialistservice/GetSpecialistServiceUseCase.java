package ru.belov.ourabroad.api.usecases.get.specialistservice;

import ru.belov.ourabroad.core.domain.SpecialistService;

import java.util.Set;

public interface GetSpecialistServiceUseCase {
    Set<SpecialistService> getBySpecialist(String specialistProfileId);

    SpecialistService getById(String serviceId);
}

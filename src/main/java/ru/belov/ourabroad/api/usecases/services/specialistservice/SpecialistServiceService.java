package ru.belov.ourabroad.api.usecases.services.specialistservice;

import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistService;

import java.util.Set;

public interface SpecialistServiceService {

    SpecialistService findById(String serviceId, Context context);
    Set<SpecialistService> findAllById(String specialistProfileId, Context context);
    void update(SpecialistService specialistService);

    void saveNew(SpecialistService specialistService, Context context);

    void deleteById(String serviceId, Context context);

    /**
     * Услуга существует, принадлежит профилю специалиста и активна (сценарий booking).
     */
    SpecialistService requireActiveServiceForSpecialist(String serviceId, String specialistProfileId, Context context);
}

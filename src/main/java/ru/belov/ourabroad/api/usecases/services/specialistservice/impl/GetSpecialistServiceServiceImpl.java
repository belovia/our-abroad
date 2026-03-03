package ru.belov.ourabroad.api.usecases.services.specialistservice.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.api.usecases.services.specialistservice.GetSpecialistServiceService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.poi.storage.SpecialistServiceRepository;

import java.util.Optional;
import java.util.Set;

import static ru.belov.ourabroad.web.validators.ErrorCode.SPECIALIST_SERVICE_NOT_FOUND;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetSpecialistServiceServiceImpl implements GetSpecialistServiceService {

    private final SpecialistServiceRepository repository;

    @Override
    public SpecialistService findById(String serviceId, Context context) {
        log.info("[serviceId: {}] Try to find specialistService by id", serviceId);

        Optional<SpecialistService> fromDbOpt = repository.findById(serviceId);
        log.info("[serviceId: {}] Try to find specialistService by id", serviceId);

        if (fromDbOpt.isEmpty()) {
            log.warn("[serviceId: {}] SpecialistProfile not found", serviceId);
            context.setError(SPECIALIST_SERVICE_NOT_FOUND);
            return null;
        }
        SpecialistService specialistService = fromDbOpt.get();
        log.info("[specialistProfileId: {}] Found: {}", serviceId, specialistService);

        return specialistService;
    }

    @Override
    public Set<SpecialistService> findAllById(String specialistProfileId) {
        return Set.of();
    }
}

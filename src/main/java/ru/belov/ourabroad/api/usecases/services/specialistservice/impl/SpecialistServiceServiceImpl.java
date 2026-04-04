package ru.belov.ourabroad.api.usecases.services.specialistservice.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.api.usecases.services.specialistservice.SpecialistServiceService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.poi.storage.SpecialistServiceRepository;

import java.util.Optional;
import java.util.Set;

import static ru.belov.ourabroad.web.validators.ErrorCode.REQUEST_VALIDATION_ERROR;
import static ru.belov.ourabroad.web.validators.ErrorCode.SERVICE_NOT_AVAILABLE;
import static ru.belov.ourabroad.web.validators.ErrorCode.SPECIALIST_SERVICE_NOT_FOUND;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpecialistServiceServiceImpl implements SpecialistServiceService {

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
        log.info("[serviceId: {}] Found: {}", serviceId, specialistService);

        return specialistService;
    }

    @Override
    public Set<SpecialistService> findAllById(String specialistProfileId, Context context) {
        if (!context.isSuccess()) {
            return Set.of();
        }
        log.info("[specialistProfileId: {}] Try to find specialistService by specialistProfileId", specialistProfileId);

        Set<SpecialistService> services = repository.findBySpecialistProfileId(specialistProfileId);
        log.info("[specialistProfileId: {}] Found services size: {}", specialistProfileId, services.size());
        return services;
    }

    @Override
    public void update(SpecialistService specialistService) {
        repository.update(specialistService);
    }

    @Override
    public void saveNew(SpecialistService specialistService, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        if (specialistService == null) {
            context.setError(REQUEST_VALIDATION_ERROR);
            return;
        }
        repository.save(specialistService);
    }

    @Override
    public void deleteById(String serviceId, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        boolean deleted = repository.deleteById(serviceId);
        if (!deleted) {
            context.setError(SPECIALIST_SERVICE_NOT_FOUND);
        }
    }

    @Override
    public SpecialistService requireActiveServiceForSpecialist(
            String serviceId,
            String specialistProfileId,
            Context context
    ) {
        if (!context.isSuccess()) {
            return null;
        }
        Optional<SpecialistService> opt = repository.findById(serviceId);
        if (opt.isEmpty()) {
            context.setError(SERVICE_NOT_AVAILABLE);
            return null;
        }
        SpecialistService service = opt.get();
        if (!specialistProfileId.equals(service.getSpecialistId()) || !service.isActive()) {
            context.setError(SERVICE_NOT_AVAILABLE);
            return null;
        }
        return service;
    }
}

package ru.belov.ourabroad.api.usecases.get.specialistservice.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.get.specialistservice.GetSpecialistServiceUseCase;
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.poi.storage.SpecialistServiceRepository;
import ru.belov.ourabroad.poi.storage.exceptions.SpecialistServiceNotFoundException;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetSpecialistServiceUseCaseImpl
        implements GetSpecialistServiceUseCase {

    private final SpecialistServiceRepository repository;

    @Override
    public Set<SpecialistService> getBySpecialist(String specialistProfileId) {

        log.info("[profileId: {}] Start get services by specialist", specialistProfileId);

        Set<SpecialistService> services = new HashSet<>(
                repository.findBySpecialistProfileId(specialistProfileId)
        );

        log.info("[profileId: {}] Services loaded, count={}",
                specialistProfileId, services.size());

        return services;
    }


    @Override
    public SpecialistService getById(String serviceId) {

        log.info("[serviceId: {}] Start get service by id", serviceId);

        SpecialistService service = repository.findById(serviceId)
                .orElseThrow(() -> {
                    log.info("[serviceId: {}] Service not found", serviceId);
                    return new SpecialistServiceNotFoundException(serviceId);
                });

        log.info("[serviceId: {}] Service loaded", serviceId);
        return service;
    }

}

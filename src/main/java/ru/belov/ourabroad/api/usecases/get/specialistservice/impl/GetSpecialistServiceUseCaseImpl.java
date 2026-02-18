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

        return new HashSet<>(
                repository.findBySpecialistProfileId(specialistProfileId)
        );
    }

    @Override
    public SpecialistService getById(String serviceId) {

        return repository.findById(serviceId)
                .orElseThrow(() ->
                        new SpecialistServiceNotFoundException(serviceId));
    }
}

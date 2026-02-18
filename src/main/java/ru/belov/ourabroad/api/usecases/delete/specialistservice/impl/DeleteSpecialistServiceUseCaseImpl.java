package ru.belov.ourabroad.api.usecases.delete.specialistservice.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.delete.specialistservice.DeleteSpecialistServiceUseCase;
import ru.belov.ourabroad.poi.storage.SpecialistServiceRepository;
import ru.belov.ourabroad.poi.storage.exceptions.SpecialistServiceNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteSpecialistServiceUseCaseImpl
        implements DeleteSpecialistServiceUseCase {

    private final SpecialistServiceRepository repository;

    @Override
    public void delete(String serviceId) {

        boolean deleted = repository.deleteById(serviceId);

        if (!deleted) {
            throw new SpecialistServiceNotFoundException(serviceId);
        }

        log.info("[serviceId={}] deleted", serviceId);
    }
}
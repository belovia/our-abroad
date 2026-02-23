package ru.belov.ourabroad.api.usecases.delete.specialistservice.impl;

import io.micrometer.common.util.StringUtils;
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
        if (serviceId == null || StringUtils.isBlank(serviceId)) {
            log.error("Input id is null or empty");
            throw new IllegalArgumentException();
        }
        log.info("[serviceId: {}] Start to delete service with ID: {}", serviceId, serviceId);
        boolean deleted = repository.deleteById(serviceId);

        if (!deleted) {
            log.info("[serviceId: {}] Exception while deleting service", serviceId);
            throw new SpecialistServiceNotFoundException(serviceId);
        }

        log.info("[serviceId: {}] deleted", serviceId);
    }
}
package ru.belov.ourabroad.api.usecases.change.specialistservice.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.change.specialistservice.ChangeSpecialistServiceUseCase;
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.poi.storage.SpecialistServiceRepository;
import ru.belov.ourabroad.poi.storage.exceptions.SpecialistServiceNotFoundException;
import ru.belov.ourabroad.web.dto.change.SpecialistServiceDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChangeSpecialistServiceUseCaseImpl implements ChangeSpecialistServiceUseCase {
    private final SpecialistServiceRepository repository;

    @Override
    public void change(String serviceId, SpecialistServiceDto dto) {

        if (serviceId == null || StringUtils.isBlank(serviceId)) {
            log.error("Input serviceId is null or empty");
            throw new IllegalArgumentException();
        }

        log.info("[serviceId={}] Start to update service", serviceId);

        SpecialistService service = repository.findById(serviceId)
                .orElseThrow(() -> {
                    log.info("[serviceId={}] Service not found", serviceId);
                    return new SpecialistServiceNotFoundException(serviceId);
                });

        if (dto.getTitle() != null) {
            service.setTitle(dto.getTitle());
        }

        if (dto.getPrice() != null) {
            if (dto.getPrice() < 0) {
                log.error("[serviceId={}] Invalid price={}", serviceId, dto.getPrice());
                throw new IllegalArgumentException("Invalid price");
            }
            service.setPrice(dto.getPrice());
        }

        if (dto.getDescription() != null) {
            service.setDescription(dto.getDescription());
        }

        repository.update(service);

        log.info("[serviceId={}] updated", serviceId);
    }

}

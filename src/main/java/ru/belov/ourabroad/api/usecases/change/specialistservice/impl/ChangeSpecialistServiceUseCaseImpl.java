package ru.belov.ourabroad.api.usecases.change.specialistservice.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        SpecialistService service = repository.findById(serviceId)
                .orElseThrow(() ->
                        new SpecialistServiceNotFoundException(serviceId));

        if (dto.getTitle() != null) {
            service.setTitle(dto.getTitle());
        }

        if (dto.getPrice() != null) {
            if (dto.getPrice() < 0) {
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

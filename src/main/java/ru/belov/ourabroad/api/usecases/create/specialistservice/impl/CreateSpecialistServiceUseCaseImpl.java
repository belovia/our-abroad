package ru.belov.ourabroad.api.usecases.create.specialistservice.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.belov.ourabroad.api.usecases.create.specialistservice.CreateSpecialistServiceUseCase;
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.core.domain.SpecialistServiceFactory;
import ru.belov.ourabroad.poi.storage.SpecialistServiceRepository;
import ru.belov.ourabroad.web.dto.change.SpecialistServiceDto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateSpecialistServiceUseCaseImpl
        implements CreateSpecialistServiceUseCase {

    private final SpecialistServiceRepository repository;

    @Override
    public SpecialistService create(
            String specialistProfileId,
            SpecialistServiceDto dto
    ) {

        log.info("[specialistProfileId={}] Start to create service with id={}",
                specialistProfileId,
                dto.getId());

        validate(specialistProfileId,dto);

        SpecialistService service =
                SpecialistServiceFactory.create(
                        dto.getId(),
                        specialistProfileId,
                        dto.getDescription(),
                        dto.getTitle(),
                        dto.getPrice(),
                        "USD"
                );

        repository.save(service);

        log.info("[profileId={}] service created id={}",
                specialistProfileId,
                service.getId());

        return service;
    }

    private void validate(String specialistProfileId, SpecialistServiceDto dto) {
        log.info("[specialistProfileId: {}] Validating requestDto", specialistProfileId);
        if (!StringUtils.hasText(dto.getTitle())) {
            throw new IllegalArgumentException("Service title is empty");
        }

        if (dto.getPrice() == null || dto.getPrice() < 0) {
            throw new IllegalArgumentException("Invalid price");
        }
        log.info("[specialistProfileId: {}] Validating success", dto.getId());
    }
}
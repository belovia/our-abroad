package ru.belov.ourabroad.api.usecases.delete.specialistprofile.impl;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.delete.specialistprofile.DeleteSpecialistProfileUseCase;
import ru.belov.ourabroad.poi.storage.SpecialistProfileRepository;
import ru.belov.ourabroad.poi.storage.exceptions.SpecialistServiceNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteSpecialistProfileUseCaseImpl implements DeleteSpecialistProfileUseCase {

    private SpecialistProfileRepository repository;

    @Override
    public void delete(String specialistProfileId) {
        if (specialistProfileId == null || StringUtils.isBlank(specialistProfileId)) {
            log.error("Input id is null or empty");
            throw new IllegalArgumentException();
        }
        log.info("[specialistProfileId: {}] Start to delete specialistProfile with ID: {}", specialistProfileId, specialistProfileId);
        boolean deleted = repository.deleteById(specialistProfileId);

        if (!deleted) {
            log.error("[specialistProfileId: {}] Exception while deleting specialistProfile", specialistProfileId);
            throw new SpecialistServiceNotFoundException(specialistProfileId);
        }

        log.info("[specialistProfileId: {}] deleted", specialistProfileId);
    }
}

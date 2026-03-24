package ru.belov.ourabroad.api.usecases.delete.specialistprofile.impl;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.delete.specialistprofile.DeleteSpecialistProfileUseCase;
import ru.belov.ourabroad.api.usecases.services.specialistprofile.SpecialistProfileService;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteSpecialistProfileUseCaseImpl implements DeleteSpecialistProfileUseCase {

    private final SpecialistProfileService profileService;

    @Override
    public void delete(String specialistProfileId) {
        if (specialistProfileId == null || StringUtils.isBlank(specialistProfileId)) {
            log.error("Input id is null or empty");
            throw new IllegalArgumentException();
        }

        log.info("[specialistProfileId: {}] Start to delete specialistProfile", specialistProfileId);
        profileService.delete(specialistProfileId);
        log.info("[specialistProfileId: {}] deleted", specialistProfileId);
    }
}

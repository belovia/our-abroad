package ru.belov.ourabroad.api.usecases.services.specialistprofile.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.api.usecases.services.specialistprofile.SpecialistProfileService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistProfile;
import ru.belov.ourabroad.poi.storage.SpecialistProfileRepository;
import ru.belov.ourabroad.poi.storage.exceptions.SpecialistServiceNotFoundException;

import java.util.Optional;

import static ru.belov.ourabroad.web.validators.ErrorCode.SPECIALIST_PROFILE_NOT_FOUND;
import static ru.belov.ourabroad.web.validators.ErrorCode.REQUEST_VALIDATION_ERROR;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpecialistProfileServiceImpl implements SpecialistProfileService {

    private final SpecialistProfileRepository repository;

    @Override
    public SpecialistProfile findById(String profileId, Context context) {
        log.info("[specialistProfileId: {}] Try to find specialistProfile", profileId);

        Optional<SpecialistProfile> fromDbOpt = repository.findById(profileId);

        if (fromDbOpt.isEmpty()) {
            log.warn("[specialistProfileId: {}] SpecialistProfile not found", profileId);
            context.setError(SPECIALIST_PROFILE_NOT_FOUND);
            return null;
        }

        SpecialistProfile specialistProfile = fromDbOpt.get();
        log.info("[specialistProfileId: {}] Found: {}", profileId, specialistProfile);
        return specialistProfile;
    }

    @Override
    public SpecialistProfile findByUserId(String userId, Context context) {
        log.info("[userId: {}] Try to find specialistProfile", userId);

        Optional<SpecialistProfile> fromDbOpt = repository.findByUserId(userId);

        if (fromDbOpt.isEmpty()) {
            log.warn("[userId: {}] SpecialistProfile not found", userId);
            context.setError(SPECIALIST_PROFILE_NOT_FOUND);
            return null;
        }

        SpecialistProfile specialistProfile = fromDbOpt.get();
        log.info("[userId: {}] Found: {}", userId, specialistProfile);
        return specialistProfile;
    }

    @Override
    public void save(SpecialistProfile profile, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        if (profile == null) {
            context.setError(REQUEST_VALIDATION_ERROR);
            return;
        }
        log.info("[userId: {}] Saving specialistProfile", profile.getUserId());
        repository.save(profile);
        log.info("[userId: {}] SpecialistProfile saved successfully", profile.getUserId());
    }

    @Override
    public void update(SpecialistProfile profile, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        if (profile == null) {
            context.setError(REQUEST_VALIDATION_ERROR);
            return;
        }
        log.info("[specialistProfileId: {}] Updating specialistProfile", profile.getId());
        repository.update(profile);
        log.info("[specialistProfileId: {}] SpecialistProfile updated successfully", profile.getId());
    }

    @Override
    public void delete(String profileId) {
        log.info("[specialistProfileId: {}] Deleting specialistProfile", profileId);

        boolean deleted = repository.deleteById(profileId);

        if (!deleted) {
            log.warn("[specialistProfileId: {}] SpecialistProfile not found for deletion", profileId);
            throw new SpecialistServiceNotFoundException(profileId);
        }

        log.info("[specialistProfileId: {}] SpecialistProfile deleted successfully", profileId);
    }
}

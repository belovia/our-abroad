package ru.belov.ourabroad.api.usecases.services.specialistprofile.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.api.usecases.services.specialistprofile.SpecialistProfileService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistProfile;
import ru.belov.ourabroad.poi.storage.SpecialistProfileRepository;

import java.util.Optional;

import static ru.belov.ourabroad.web.validators.ErrorCode.SPECIALIST_PROFILE_NOT_FOUND;

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
    public void update(SpecialistProfile specialist) {
        repository.update(specialist);
    }
}

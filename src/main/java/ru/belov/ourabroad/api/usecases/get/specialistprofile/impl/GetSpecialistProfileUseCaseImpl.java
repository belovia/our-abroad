package ru.belov.ourabroad.api.usecases.get.specialistprofile.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.get.specialistprofile.GetSpecialistProfileUseCase;
import ru.belov.ourabroad.api.usecases.get.specialistservice.GetSpecialistServiceUseCase;
import ru.belov.ourabroad.core.domain.SpecialistProfile;
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.poi.storage.SpecialistProfileRepository;
import ru.belov.ourabroad.poi.storage.exceptions.SpecialistProfileNotFoundException;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetSpecialistProfileUseCaseImpl
        implements GetSpecialistProfileUseCase {

    private final SpecialistProfileRepository profileRepository;
    private final GetSpecialistServiceUseCase getServiceUseCase;

    @Override
    public SpecialistProfile getByUserId(String userId) {

        log.info("[userId: {}] Start get specialist profile by userId", userId);

        SpecialistProfile profile = profileRepository
                .findByUserId(userId)
                .orElseThrow(() -> {
                    log.info("[userId: {}] Specialist profile not found", userId);
                    return new SpecialistProfileNotFoundException(userId);
                });

        loadServices(profile);

        log.info("[userId: {}] Specialist profile loaded", userId);
        return profile;
    }


    @Override
    public SpecialistProfile getById(String profileId) {

        log.info("[profileId: {}] Start get specialist profile by id", profileId);

        SpecialistProfile profile = profileRepository
                .findById(profileId)
                .orElseThrow(() -> {
                    log.info("[profileId: {}] Specialist profile not found", profileId);
                    return new SpecialistProfileNotFoundException(profileId);
                });

        loadServices(profile);

        log.info("[profileId: {}] Specialist profile loaded", profileId);
        return profile;
    }

    protected void loadServices(SpecialistProfile profile) {
        log.info("[profileId: {}] Loading services", profile.getId());

        Set<SpecialistService> services =
                getServiceUseCase.getBySpecialist(profile.getId());

        profile.setServices(services);

        log.info("[profileId: {}] Services loaded, count={}",
                profile.getId(), services.size());
    }
}

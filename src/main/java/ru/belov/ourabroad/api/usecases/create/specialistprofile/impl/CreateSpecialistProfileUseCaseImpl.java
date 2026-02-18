package ru.belov.ourabroad.api.usecases.create.specialistprofile.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.belov.ourabroad.api.usecases.create.specialistprofile.CreateSpecialistProfileUseCase;
import ru.belov.ourabroad.core.domain.SpecialistProfile;
import ru.belov.ourabroad.core.domain.SpecialistProfileFactory;
import ru.belov.ourabroad.poi.storage.SpecialistProfileRepository;
import ru.belov.ourabroad.poi.storage.exceptions.SpecialistProfileAlreadyExistsException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateSpecialistProfileUseCaseImpl
        implements CreateSpecialistProfileUseCase {

    private final SpecialistProfileRepository profileRepository;

    @Override
    public SpecialistProfile create(String userId, String description) {

        if (!StringUtils.hasText(userId)) {
            throw new IllegalArgumentException("userId is empty");
        }

        profileRepository.findByUserId(userId)
                .ifPresent(p -> {
                    throw new SpecialistProfileAlreadyExistsException(userId);
                });

        SpecialistProfile profile =
                SpecialistProfileFactory.create(userId, description);

        profileRepository.save(profile);

        log.info("[userId={}] specialist profile created", userId);

        return profile;
    }
}
package ru.belov.ourabroad.api.usecases.get.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.get.user.GetUserProfileUseCase;
import ru.belov.ourabroad.core.domain.Profile;
import ru.belov.ourabroad.poi.storage.ProfileRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserProfileUseCaseImpl implements GetUserProfileUseCase {

    private final ProfileRepository profileRepository;
    @Override
    public Profile getByUserId(String userId) {

        if (userId == null || StringUtils.isBlank(userId)) {
            log.warn("GetProfileByUserId called with empty userId");
            return null;
        }

        log.info("[userId: {}] Start get profile by userId", userId);

        Optional<Profile> profileOpt = profileRepository.findByUserId(userId);

        if (profileOpt.isEmpty()) {
            log.info("[userId: {}] Profile not found", userId);
            return null;
        }

        log.info("[userId: {}] Profile loaded", userId);
        return profileOpt.get();
    }

}

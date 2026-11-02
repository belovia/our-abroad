package ru.belov.ourabroad.api.usecases.services.profile.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.api.usecases.services.profile.ProfileService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Profile;
import ru.belov.ourabroad.core.domain.ProfileFactory;
import ru.belov.ourabroad.poi.storage.ProfileRepository;
import ru.belov.ourabroad.web.validators.ErrorCode;

@Component
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;

    @Override
    public void saveEmpty(String userId, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        profileRepository.save(ProfileFactory.createEmpty(userId));
    }

    @Override
    public Profile findByUserId(String userId, Context context) {
        if (!context.isSuccess()) {
            return null;
        }
        return profileRepository.findByUserId(userId).orElseGet(() -> {
            context.setError(ErrorCode.PROFILE_NOT_FOUND);
            return null;
        });
    }

    @Override
    public void update(Profile profile, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        profileRepository.update(profile);
    }
}

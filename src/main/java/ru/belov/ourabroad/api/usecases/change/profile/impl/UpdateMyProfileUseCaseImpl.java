package ru.belov.ourabroad.api.usecases.change.profile.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.belov.ourabroad.api.usecases.change.profile.UpdateMyProfileUseCase;
import ru.belov.ourabroad.api.usecases.services.profile.ProfileService;
import ru.belov.ourabroad.config.security.CurrentUserProvider;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Profile;
import ru.belov.ourabroad.web.validators.UserValidator;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateMyProfileUseCaseImpl implements UpdateMyProfileUseCase {

    private final ProfileService profileService;
    private final CurrentUserProvider currentUserProvider;
    private final UserValidator userValidator;

    @Override
    @Transactional
    public Response execute(Request request) {
        Context context = new Context();
        String userId = currentUserProvider.requiredUserId();
        log.info("[userId: {}] Update cabinet profile", userId);

        Profile profile = profileService.findByUserId(userId, context);
        if (profile == null) {
            return error(userId, context);
        }

        String displayName = profile.getDisplayName();
        if (request.displayName() != null) {
            displayName = request.displayName().trim();
            userValidator.validateDisplayName(displayName, context);
        }
        String bio = request.bio() != null ? request.bio() : profile.getBio();
        userValidator.validateBio(request.bio(), context);
        if (!context.isSuccess()) {
            return error(userId, context);
        }

        Set<String> languages = request.languages() != null ? request.languages() : profile.getLanguages();
        if (languages == null) {
            languages = Set.of();
        }

        profile.update(
                displayName,
                request.avatarUrl() != null ? request.avatarUrl() : profile.getAvatarUrl(),
                bio,
                request.country() != null ? request.country() : profile.getCountry(),
                request.city() != null ? request.city() : profile.getCity(),
                languages
        );
        profileService.update(profile, context);
        if (!context.isSuccess()) {
            return error(userId, context);
        }

        context.setSuccessResult();
        return new Response(
                true,
                context.getErrorMessage(),
                profile.getUserId(),
                profile.getDisplayName(),
                profile.getAvatarUrl(),
                profile.getBio(),
                profile.getCountry(),
                profile.getCity(),
                profile.getLanguages()
        );
    }

    private Response error(String userId, Context context) {
        return new Response(false, context.getErrorMessage(), userId, null, null, null, null, null, null);
    }
}

package ru.belov.ourabroad.api.usecases.get.profile.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.get.profile.GetMyProfileUseCase;
import ru.belov.ourabroad.api.usecases.services.profile.ProfileService;
import ru.belov.ourabroad.config.security.CurrentUserProvider;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Profile;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetMyProfileUseCaseImpl implements GetMyProfileUseCase {

    private final ProfileService profileService;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public Response execute() {
        Context context = new Context();
        String userId = currentUserProvider.requiredUserId();
        log.info("[userId: {}] Get cabinet profile", userId);

        Profile profile = profileService.findByUserId(userId, context);
        if (profile == null) {
            return new Response(false, context.getErrorMessage(), userId, null, null, null, null, null, null);
        }

        context.setSuccessResult();
        return toResponse(profile, true, context.getErrorMessage());
    }

    static Response toResponse(Profile profile, boolean success, String message) {
        return new Response(
                success,
                message,
                profile.getUserId(),
                profile.getDisplayName(),
                profile.getAvatarUrl(),
                profile.getBio(),
                profile.getCountry(),
                profile.getCity(),
                profile.getLanguages()
        );
    }
}

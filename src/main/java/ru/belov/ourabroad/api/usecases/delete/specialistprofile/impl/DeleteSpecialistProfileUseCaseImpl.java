package ru.belov.ourabroad.api.usecases.delete.specialistprofile.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.belov.ourabroad.api.usecases.delete.specialistprofile.DeleteSpecialistProfileUseCase;
import ru.belov.ourabroad.api.usecases.services.specialistprofile.SpecialistProfileService;
import ru.belov.ourabroad.config.security.CurrentUserProvider;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistProfile;
import ru.belov.ourabroad.web.validators.ErrorCode;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteSpecialistProfileUseCaseImpl implements DeleteSpecialistProfileUseCase {

    private final SpecialistProfileService profileService;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public Response delete(String specialistProfileId) {
        String userId = currentUserProvider.requiredUserId();
        Context context = new Context();

        if (!StringUtils.hasText(specialistProfileId)) {
            log.error("Input id is null or empty");
            return new Response(false, ErrorCode.REQUEST_VALIDATION_ERROR.getMessage());
        }

        log.info("[userId: {}][specialistProfileId: {}] Start to delete specialistProfile", userId, specialistProfileId);

        SpecialistProfile profile = profileService.findById(specialistProfileId, context);
        if (!context.isSuccess()) {
            return new Response(false, context.getErrorMessage());
        }
        if (!userId.equals(profile.getUserId())) {
            context.setError(ErrorCode.ACCESS_DENIED);
            return new Response(false, context.getErrorMessage());
        }

        profileService.delete(specialistProfileId, context);
        if (!context.isSuccess()) {
            return new Response(false, context.getErrorMessage());
        }

        log.info("[userId: {}][specialistProfileId: {}] deleted", userId, specialistProfileId);
        return new Response(true, ErrorCode.SUCCESS.getMessage());
    }
}

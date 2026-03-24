package ru.belov.ourabroad.api.usecases.create.specialistprofile.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.belov.ourabroad.api.usecases.create.specialistprofile.CreateSpecialistProfileUseCase;
import ru.belov.ourabroad.api.usecases.services.specialistprofile.SpecialistProfileService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistProfile;
import ru.belov.ourabroad.core.domain.SpecialistProfileFactory;

import static ru.belov.ourabroad.web.validators.ErrorCode.USER_ID_REQUIRED;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateSpecialistProfileUseCaseImpl implements CreateSpecialistProfileUseCase {

    private final SpecialistProfileService profileService;

    @Override
    public Response execute(Request request) {
        Context context = new Context();
        String userId = request.userId();
        log.info("[userId: {}] Start to create specialistProfileId", userId);

        validate(userId, context);
        if (!context.isSuccess()) {
            log.info("[userId: {}] Validation failed", userId);
            return errorResponse(userId, context);
        }

        SpecialistProfile profile = makeSpecialistProfile(request, userId);

        createSpecialistProfile(profile, context);

        log.info("[userId: {}] specialist profile created", userId);
        return successResponse(userId);
    }

    private void validate(String userId, Context context) {
        log.info("Validating inputID");

        if (!StringUtils.hasText(userId)) {
            log.info("Validation failed");
            context.setError(USER_ID_REQUIRED);
            return;
        }
        log.info("[userId: {}] Validating success", userId);
    }

    protected SpecialistProfile makeSpecialistProfile(Request request, String userId) {
        return SpecialistProfileFactory.create(userId, request.description());
    }

    protected void createSpecialistProfile(SpecialistProfile profile, Context context) {
        profileService.save(profile, context);
    }

    protected Response errorResponse(String userId, Context context) {
        log.error("[userId: {}] Returning error response", userId);
        return new Response(userId, false, context.getErrorCode().getMessage());
    }

    protected Response successResponse(String userId) {
        log.info("[userId: {}] Returning success response", userId);
        return new Response(userId, true, null);
    }
}

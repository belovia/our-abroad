package ru.belov.ourabroad.api.usecases.change.specialistprofile.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.change.specialistprofile.ChangeSpecialistProfileUseCase;
import ru.belov.ourabroad.api.usecases.services.specialistprofile.SpecialistProfileService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistProfile;
import ru.belov.ourabroad.web.validators.FieldValidator;

import static ru.belov.ourabroad.web.validators.ErrorCode.DB_ERROR;
import static ru.belov.ourabroad.web.validators.ErrorCode.SUCCESS;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChangeSpecialistProfileUseCaseImpl implements ChangeSpecialistProfileUseCase {

    private final SpecialistProfileService service;
    private final FieldValidator validator;

    @Override
    public Response execute(Request request) {
        Context context = new Context();
        String profileId = request.profileId();
        log.info("[profileId: {}] Start change specialist profile", profileId);

        validateRequiredFields(request, context);
        if (!context.isSuccess()) {
            log.info("[profileId: {}] Validation failed", profileId);
            return errorResponse(profileId, context);
        }

        SpecialistProfile fromDb = retrieveSpecialistProfile(profileId, context);
        if (!context.isSuccess() || fromDb == null) {
            log.info("[profileId: {}] Failed to retrieve specialist profile", profileId);
            return errorResponse(profileId, context);
        }
        log.info("[profileId: {}] Found: {}", profileId, fromDb);

        fromDb.setDescription(request.description());

        return updateAndReturnResponse(fromDb, profileId, context);
    }


    protected void validateRequiredFields(Request request, Context context) {
        log.info("Validating request");
        validator.validateRequiredField(request.profileId(), context);
        validator.validateRequiredField(request.description(), context);
        if (context.isSuccess()) {
            log.info("Validation success");
        } else {
            log.error("Validation failed");
        }
    }

    protected SpecialistProfile retrieveSpecialistProfile(String profileId, Context context) {
        log.info("[profileId: {}] Try to find specialistProfile by id", profileId);
        return service.findById(profileId, context);
    }

    protected Response updateAndReturnResponse(SpecialistProfile fromDb, String profileId, Context context) {
        try {
            service.update(fromDb, context);
            return successResponse(profileId);
        } catch (Exception e) {
            log.error("[profileId: {}] Error while updating specialistProfile", profileId);
            context.setError(DB_ERROR);
            return errorResponse(profileId, context);
        }
    }

    protected Response errorResponse(String profileId, Context context) {
        return new Response(profileId, false, context.getErrorMessage());
    }

    protected Response successResponse(String profileId) {
        return new Response(profileId, true, SUCCESS.getMessage());
    }
}

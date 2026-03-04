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

@Service
@RequiredArgsConstructor
@Slf4j
public class ChangeSpecialistProfileUseCaseImpl implements ChangeSpecialistProfileUseCase {

    private final SpecialistProfileService service;
    private final FieldValidator validator;

    @Override
    public Response execute(Request request) {
        Context context = new Context();
        validateRequiredFields(request, context);

        String profileId = request.profileId();

        SpecialistProfile fromDb = retrieveSpecialistProfile(profileId, context);

        if (!context.isSuccess()) {
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
        if (!context.isSuccess()) {
            log.warn("[profileId: {}] Context is failed", profileId);
            return null;
        }
        return service.findById(profileId, context);
    }

    protected Response updateAndReturnResponse(SpecialistProfile fromDb, String profileId, Context context) {
        try {
            service.update(fromDb);
            return successResponse(profileId, context);
        } catch (Exception e) {
            log.error("[profileId: {}] Error while updating specialistProfile", profileId);
            context.setError(DB_ERROR);
            return errorResponse(profileId, context);
        }
    }

    protected Response errorResponse(String profileId, Context context) {
        return new Response(profileId, context.isSuccess(), context.getErrorCode().getMessage());
    }

    protected Response successResponse(String profileId, Context context) {
        return new Response(profileId, context.isSuccess(), null);
    }
}

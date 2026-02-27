package ru.belov.ourabroad.api.usecases.get.specialistprofile.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.get.specialistprofile.GetSpecialistProfileByIdUseCase;
import ru.belov.ourabroad.api.usecases.get.specialistservice.GetSpecialistServiceUseCase;
import ru.belov.ourabroad.api.usecases.services.specialistprofile.SpecialistProfileService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistProfile;
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.web.validators.SpecialistProfileValidator;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetSpecialistProfileByIdUseCaseImpl
        implements GetSpecialistProfileByIdUseCase {

    private final SpecialistProfileService service;
    private final GetSpecialistServiceUseCase getServiceUseCase;
    private final SpecialistProfileValidator validator;

    @Override
    public Response execute(Request request) {
        Context context = new Context();
        validateRequest(request, context);

        String specialistProfileId = request.specialistProfileId();
        log.info("[specialistProfileId: {}] Start get specialist profile by userId", specialistProfileId);

        SpecialistProfile fromDb = retrieveSpecialistProfile(specialistProfileId, context);
        if (!context.isSuccess()) {
            log.info("[specialistProfileId: {}] Context is failed, return error response", specialistProfileId);
            return errorResponse(context);
        }

        loadServices(fromDb);

        log.info("[specialistProfileId: {}] Specialist profile loaded", specialistProfileId);
        log.info("[specialistProfileId: {}] Return success response", specialistProfileId);

        return successResponse(fromDb, context);
    }

    private SpecialistProfile retrieveSpecialistProfile(String specialistProfileId, Context context) {
        log.info("[specialistProfileId: {}] Try to find specialistProfile by id", specialistProfileId);
        return service.findById(specialistProfileId, context);
    }

    protected void loadServices(SpecialistProfile profile) {
        log.info("[specialistProfileId: {}] Loading services", profile.getId());

        Set<SpecialistService> services =
                getServiceUseCase.getBySpecialist(profile.getId());

        profile.setServices(services);

        log.info("[specialistProfileId: {}] Services loaded, count: {}",
                profile.getId(), services.size());
    }

    protected void validateRequest(Request request, Context context) {
        log.info("Validating request");
        validator.validateRequest(request, context);
        validator.validateRequiredField(request.specialistProfileId(), context);
        if (context.isSuccess()) {
            log.info("Validation success");
        } else {
            log.error("Validation failed");
        }
    }

    protected Response errorResponse(Context context) {
        return new Response(null, context.isSuccess(), context.getErrorCode().getMessage());
    }

    protected Response successResponse(SpecialistProfile specialistProfile, Context context) {
        return new Response(specialistProfile, context.isSuccess(), null);
    }


}

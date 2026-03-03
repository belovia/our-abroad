package ru.belov.ourabroad.api.usecases.get.specialistprofile.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.get.specialistprofile.GetSpecialistProfileByUserIdUseCase;
import ru.belov.ourabroad.api.usecases.get.specialistservice.GetSpecialistServiceByServiceIdUseCase;
import ru.belov.ourabroad.api.usecases.services.specialistprofile.SpecialistProfileService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistProfile;
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.web.validators.FieldValidator;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetSpecialistProfileByUserIdUseCaseImpl implements GetSpecialistProfileByUserIdUseCase {

    private final SpecialistProfileService service;
    private final GetSpecialistServiceByServiceIdUseCase getServiceUseCase;
    private final FieldValidator validator;

    @Override
    public Response execute(Request request) {
        Context context = new Context();
        validateRequest(request, context);

        String userId = request.userId();
        log.info("[userId: {}] Start get specialist profile by userId", userId);

        SpecialistProfile fromDb = retrieveSpecialistProfile(userId, context);
        if (!context.isSuccess()) {
            log.info("[userId: {}] Context is failed, return error response", userId);
            return errorResponse(context);
        }

        loadServices(fromDb);
        log.info("[userId: {}] Specialist profile loaded", userId);
        log.info("[userId: {}] Return success response", userId);

        return successResponse(fromDb, context);
    }


    protected void validateRequest(Request request, Context context) {
        log.info("Validating request");
        validator.validateRequest(request, context);
        validator.validateRequiredField(request.userId(), context);
        if (context.isSuccess()) {
            log.info("Validation success");
        } else {
            log.error("Validation failed");
        }
    }

    private SpecialistProfile retrieveSpecialistProfile(String userId, Context context) {
        log.info("[userId: {}] Try to find specialistProfile by id", userId);
        return service.findById(userId, context);
    }

    protected void loadServices(SpecialistProfile profile) {
        log.info("[userId: {}] Loading services", profile.getUserId());

        Set<SpecialistService> services =
                getServiceUseCase.getBySpecialist(profile.getId());

        profile.setServices(services);

        log.info("[userId: {}] Services loaded, count: {}",
                profile.getId(), services.size());
    }

    protected Response errorResponse(Context context) {
        return new Response(null, context.isSuccess(), context.getErrorCode().getMessage());
    }

    protected Response successResponse(SpecialistProfile specialistProfile, Context context) {
        return new Response(specialistProfile, context.isSuccess(), null);
    }
}



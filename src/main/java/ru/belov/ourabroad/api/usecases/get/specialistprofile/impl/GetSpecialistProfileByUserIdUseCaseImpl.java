package ru.belov.ourabroad.api.usecases.get.specialistprofile.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.get.specialistprofile.GetSpecialistProfileByUserIdUseCase;
import ru.belov.ourabroad.api.usecases.services.specialistprofile.SpecialistProfileService;
import ru.belov.ourabroad.api.usecases.get.specialistservice.GetServicesByProfileIdUseCase;
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
    private final GetServicesByProfileIdUseCase getSpecialistServicesUsecase;
    private final FieldValidator validator;

    @Override
    public Response execute(Request request) {
        Context context = new Context();
        String userId = request.userId();
        log.info("[userId: {}] Start get specialist profile by userId", userId);

        validateRequest(request, context);
        if (!context.isSuccess()) {
            log.info("[userId: {}] Validation failed", userId);
            return errorResponse(context);
        }

        SpecialistProfile profile = retrieveSpecialistProfile(userId, context);
        if (!context.isSuccess() || profile == null) {
            log.info("[userId: {}] Failed to retrieve specialist profile", userId);
            return errorResponse(context);
        }

        loadServices(profile);

        log.info("[userId: {}] Specialist profile loaded successfully", userId);
        return successResponse(profile);
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

        var request = prepareRequestForServices(profile.getId());

        Set<SpecialistService> services
                = getSpecialistServices(request);

        profile.setServices(services);

        log.info("[userId: {}] Services loaded, count: {}",
                profile.getId(), services.size());
    }

    private Set<SpecialistService> getSpecialistServices(GetServicesByProfileIdUseCase.Request request) {
        return getSpecialistServicesUsecase.execute(request).services();
    }

    protected Response errorResponse(Context context) {
        return new Response(null, false, context.getErrorCode().getMessage());
    }

    protected Response successResponse(SpecialistProfile specialistProfile) {
        return new Response(specialistProfile, true, null);
    }

    private GetServicesByProfileIdUseCase.Request prepareRequestForServices(String specialistProfileId) {
        return new GetServicesByProfileIdUseCase.Request(specialistProfileId);
    }
}



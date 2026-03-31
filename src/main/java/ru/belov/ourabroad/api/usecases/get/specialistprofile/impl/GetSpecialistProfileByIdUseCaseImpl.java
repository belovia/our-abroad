package ru.belov.ourabroad.api.usecases.get.specialistprofile.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.get.specialistprofile.GetSpecialistProfileByIdUseCase;
import ru.belov.ourabroad.api.usecases.get.specialistservice.GetServicesByProfileIdUseCase;
import ru.belov.ourabroad.api.usecases.services.specialistprofile.SpecialistProfileService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistProfile;
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.FieldValidator;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetSpecialistProfileByIdUseCaseImpl
        implements GetSpecialistProfileByIdUseCase {

    private final SpecialistProfileService service;
    private final GetServicesByProfileIdUseCase getServiceUseCase;
    private final FieldValidator validator;

    @Override
    public Response execute(Request request) {
        Context context = new Context();
        String specialistProfileId = request.specialistProfileId();
        log.info("[specialistProfileId: {}] Start get specialist profile by id", specialistProfileId);

        validateRequest(request, context);
        if (!context.isSuccess()) {
            log.info("[specialistProfileId: {}] Validation failed", specialistProfileId);
            return errorResponse(context);
        }

        SpecialistProfile profile = retrieveSpecialistProfile(specialistProfileId, context);
        if (!context.isSuccess() || profile == null) {
            log.info("[specialistProfileId: {}] Failed to retrieve specialist profile", specialistProfileId);
            return errorResponse(context);
        }

        loadServices(profile);

        log.info("[specialistProfileId: {}] Specialist profile loaded successfully", specialistProfileId);
        return successResponse(profile);
    }

    private SpecialistProfile retrieveSpecialistProfile(String specialistProfileId, Context context) {
        log.info("[specialistProfileId: {}] Try to find specialistProfile by id", specialistProfileId);
        return service.findById(specialistProfileId, context);
    }

    protected void loadServices(SpecialistProfile profile) {
        log.info("[specialistProfileId: {}] Loading services", profile.getId());

        var request = prepareRequestForServices(profile.getId());

        Set<SpecialistService> services
                = getSpecialistServices(request);

        profile.setServices(services);

        log.info("[userId: {}] Services loaded, count: {}",
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
        return new Response(null, false, context.getErrorMessage());
    }

    protected Response successResponse(SpecialistProfile specialistProfile) {
        return new Response(specialistProfile, true, ErrorCode.SUCCESS.getMessage());
    }

    private Set<SpecialistService> getSpecialistServices(GetServicesByProfileIdUseCase.Request request) {
        return getServiceUseCase.execute(request).services();
    }

    private GetServicesByProfileIdUseCase.Request prepareRequestForServices(String specialistProfileId) {
        return new GetServicesByProfileIdUseCase.Request(specialistProfileId);
    }


}

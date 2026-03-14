package ru.belov.ourabroad.api.usecases.get.specialistservice.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.get.specialistservice.GetServicesByProfileIdUseCase;
import ru.belov.ourabroad.api.usecases.services.specialistservice.GetSpecialistServiceService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.web.validators.FieldValidator;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetServicesByProfileIdServiceImpl implements GetServicesByProfileIdUseCase {

    private final GetSpecialistServiceService service;
    private final FieldValidator validator;


    @Override
    public Response execute(Request request) {
        Context context = new Context();
        validateRequest(request, context);

        String specialistProfileId = request.specialistProfileId();

        log.info("[specialistProfileId: {}] Start get services by specialistId", specialistProfileId);
        Set<SpecialistService> services = findProfileServices(specialistProfileId, context);

        if (!context.isSuccess()) {
            log.warn("[specialistProfileId: {}] Returning error response", specialistProfileId);
            return errorResponse(context);
        } else {
            log.info("[specialistProfileId: {}] Returning success response", specialistProfileId);
            return successResponse(services);
        }
    }

    private Set<SpecialistService> findProfileServices(String specialistProfileId, Context context) {
        if (!context.isSuccess()) {
            log.warn("[profileId: {}] Failed to start getting profile services", specialistProfileId);
            return Set.of();
        }
        return service.findAllById(specialistProfileId, context);
    }


    protected void  validateRequest(Request request, Context context) {
        log.info("Validating request");
        validator.validateRequiredField(request.specialistProfileId(), context);
        if (context.isSuccess()) {
            log.info("Validation success");
        } else {
            log.error("Validation failed");
        }
    }

    protected Response errorResponse(Context context) {
        return new Response(null, false, context.getErrorCode().getMessage());
    }

    protected Response successResponse(Set<SpecialistService> services) {
        return new Response(services, true, null);
    }
}

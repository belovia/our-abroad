package ru.belov.ourabroad.api.usecases.get.specialistservice.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.get.specialistservice.GetServicesByProfileIdUseCase;
import ru.belov.ourabroad.api.usecases.services.specialistservice.SpecialistServiceService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.FieldValidator;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetServicesByProfileIdServiceImpl implements GetServicesByProfileIdUseCase {

    private final SpecialistServiceService service;
    private final FieldValidator validator;


    @Override
    public Response execute(Request request) {
        Context context = new Context();
        String specialistProfileId = request.specialistProfileId();
        log.info("[specialistProfileId: {}] Start get services by specialistId", specialistProfileId);

        validateRequest(request, context);
        if (!context.isSuccess()) {
            log.info("[specialistProfileId: {}] Validation failed", specialistProfileId);
            return errorResponse(context);
        }

        Set<SpecialistService> services = findProfileServices(specialistProfileId, context);
        if (!context.isSuccess()) {
            log.info("[specialistProfileId: {}] Failed to retrieve services", specialistProfileId);
            return errorResponse(context);
        }

        log.info("[specialistProfileId: {}] Services loaded successfully, count: {}", specialistProfileId, services.size());
        return successResponse(services);
    }

    private Set<SpecialistService> findProfileServices(String specialistProfileId, Context context) {
        log.info("[specialistProfileId: {}] Try to find services by profileId", specialistProfileId);
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
        return new Response(null, false, context.getErrorMessage());
    }

    protected Response successResponse(Set<SpecialistService> services) {
        return new Response(services, true, ErrorCode.SUCCESS.getMessage());
    }
}

package ru.belov.ourabroad.api.usecases.get.specialistservice.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.get.specialistservice.GetSpecialistServiceByServiceIdUseCase;
import ru.belov.ourabroad.api.usecases.services.specialistservice.SpecialistServiceService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.FieldValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetSpecialistServiceByServiceIdUseCaseImpl
        implements GetSpecialistServiceByServiceIdUseCase {


    private final FieldValidator validator;
    private final SpecialistServiceService service;


    @Override
    public Response execute(Request request) {
        Context context = new Context();
        String serviceId = request.serviceId();
        log.info("[serviceId: {}] Start get service by id", serviceId);

        validateRequest(request, context);
        if (!context.isSuccess()) {
            log.info("[serviceId: {}] Validation failed", serviceId);
            return errorResponse(context);
        }

        SpecialistService specialistService = findById(serviceId, context);
        if (!context.isSuccess() || specialistService == null) {
            log.info("[serviceId: {}] Failed to retrieve service", serviceId);
            return errorResponse(context);
        }

        log.info("[serviceId: {}] Service loaded successfully", serviceId);
        return successResponse(specialistService);
    }

    private SpecialistService findById(String serviceId, Context context) {
        log.info("[serviceId: {}] Try to find service by id", serviceId);
        return service.findById(serviceId, context);
    }

    protected void validateRequest(Request request, Context context) {
        log.info("Validating request");
        validator.validateRequiredField(request.serviceId(), context);
        if (context.isSuccess()) {
            log.info("Validation success");
        } else {
            log.error("Validation failed");
        }
    }

    protected Response errorResponse(Context context){
        return new Response(null, false, context.getErrorMessage());
    }

    protected Response successResponse(SpecialistService specialistService){
        return new Response(specialistService, true, ErrorCode.SUCCESS.getMessage());
    }

}

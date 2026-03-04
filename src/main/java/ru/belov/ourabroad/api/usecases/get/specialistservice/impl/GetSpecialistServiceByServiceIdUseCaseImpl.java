package ru.belov.ourabroad.api.usecases.get.specialistservice.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.get.specialistservice.GetSpecialistServiceByServiceIdUseCase;
import ru.belov.ourabroad.api.usecases.services.specialistservice.GetSpecialistServiceService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.web.validators.FieldValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetSpecialistServiceByServiceIdUseCaseImpl
        implements GetSpecialistServiceByServiceIdUseCase {


    private final FieldValidator validator;
    private final GetSpecialistServiceService service;


    @Override
    public Response execute(Request request) {

        Context context = new Context();
        validateRequest(request, context);
        String serviceId = request.serviceId();

        log.info("[serviceId: {}] Start get service by id", serviceId);

        SpecialistService specialistService = findById(serviceId, context);
        log.info("[serviceId: {}] Service found", specialistService);

        if (!context.isSuccess()) {
            log.warn("[serviceId: {}] Returning error response", serviceId);
            return errorResponse(context);
        }
        log.info("[serviceId: {}] Returning success response", serviceId);
        return successResponse(specialistService, context);
    }

    private SpecialistService findById(String serviceId, Context context) {
        if (!context.isSuccess()) {
            return null;
        }
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
        return new Response(null, context.isSuccess(), context.getErrorCode().getMessage());
    }

    protected Response successResponse(SpecialistService specialistService, Context context){
        return new Response(specialistService, context.isSuccess(), null);
    }

}

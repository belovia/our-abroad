package ru.belov.ourabroad.api.usecases.change.specialistservice.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.change.specialistservice.ChangeSpecialistServiceUseCase;
import ru.belov.ourabroad.api.usecases.services.specialistservice.SpecialistServiceService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.web.validators.FieldValidator;

import static ru.belov.ourabroad.web.validators.ErrorCode.PRICE_MUST_BE_BIGGER_THAN_ZERO;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChangeSpecialistServiceUseCaseImpl implements ChangeSpecialistServiceUseCase {

    private final SpecialistServiceService specialistServiceService;
    private final FieldValidator validator;

    @Override
    public Response execute(Request request) {
        Context context = new Context();
        String serviceId = request.serviceId();
        log.info("[serviceId: {}] Start to update service", serviceId);

        validateRequiredFields(request, context);
        if (!context.isSuccess()) {
            log.info("[serviceId: {}] Validation failed", serviceId);
            return errorResponse(serviceId, context);
        }

        SpecialistService service = retrieveService(serviceId, context);
        if (!context.isSuccess() || service == null) {
            log.info("[serviceId: {}] Failed to retrieve service", serviceId);
            return errorResponse(serviceId, context);
        }

        applyChanges(request, service, context);
        if (!context.isSuccess()) {
            log.info("[serviceId: {}] Failed to apply changes", serviceId);
            return errorResponse(serviceId, context);
        }

        specialistServiceService.update(service);

        log.info("[serviceId: {}] Service updated successfully", serviceId);
        return successResponse(serviceId);
    }

    private void applyChanges(Request request, SpecialistService service, Context context) {
        String serviceId = request.serviceId();

        if (request.title() != null) {
            service.setTitle(request.title());
        }
        if (request.price() != null) {
            if (request.price() < 0) {
                log.error("[serviceId: {}] Invalid price: {}", serviceId, request.price());
                context.setError(PRICE_MUST_BE_BIGGER_THAN_ZERO);
                return;
            }
            service.setPrice(request.price());
        }
        if (request.description() != null) {
            service.setDescription(request.description());
        }
    }

    protected void validateRequiredFields(Request request, Context context) {
        log.info("[serviceId: {}] Validating required fields", request.serviceId());
        validator.validateRequiredField(request.serviceId(), context);
        validator.validateRequiredField(request.title(), context);
        validator.validateRequiredField(request.price(), context);
        validator.validateRequiredField(request.description(), context);
        if (context.isSuccess()) {
            log.info("Validation success");
        } else {
            log.error("Validation failed");
        }
    }

    private SpecialistService retrieveService(String serviceId, Context context) {
        log.info("[serviceId: {}] Try to find service by id", serviceId);
        return specialistServiceService.findById(serviceId, context);
    }

    protected Response errorResponse(String serviceId, Context context) {
        log.error("[serviceId: {}] Returning error response", serviceId);
        return new Response(serviceId, false, context.getErrorCode().getMessage());
    }

    protected Response successResponse(String serviceId) {
        log.info("[serviceId: {}] Returning success response", serviceId);
        return new Response(serviceId, true, null);
    }
}

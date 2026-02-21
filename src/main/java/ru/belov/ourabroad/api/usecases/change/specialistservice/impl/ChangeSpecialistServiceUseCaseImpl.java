package ru.belov.ourabroad.api.usecases.change.specialistservice.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.belov.ourabroad.api.usecases.change.specialistservice.ChangeSpecialistServiceUseCase;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.poi.storage.SpecialistServiceRepository;
import ru.belov.ourabroad.web.validators.SpecialistServiceValidator;

import java.util.Optional;

import static ru.belov.ourabroad.web.validators.ErrorCode.PRICE_MUST_BE_BIGGER_THAN_ZERO;
import static ru.belov.ourabroad.web.validators.ErrorCode.SPECIALIST_SERVICE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChangeSpecialistServiceUseCaseImpl implements ChangeSpecialistServiceUseCase {

    private final SpecialistServiceRepository repository;
    private final SpecialistServiceValidator validator;

    @Override
    public Response execute(Request request) {

        Context context = new Context();
        String serviceId = request.serviceId();

        validateRequiredFields(request, context);
        log.info("[serviceId: {}] Start to update service", serviceId);

        SpecialistService service = retrieveService(serviceId, context);

        if (!context.isSuccess() || service == null) {
            return errorResponse(request, context);
        }

        fillServiceFields(request, context, serviceId, service);

        if (!context.isSuccess()) {
            return errorResponse(request, context);
        }

        repository.update(service);

        log.info("[serviceId: {}] Updating success", serviceId);

        return successResponse(request);
    }

    private void fillServiceFields(Request request, Context context, String serviceId, SpecialistService service) {
        if (service == null) {
            return;
        }

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
        log.info("[serviceId: {}] validate required fields", request.serviceId());
        validator.validateRequiredField(request.serviceId(), context);
        validator.validateRequiredField(request.title(), context);
        validator.validateRequiredField(request.price(), context);
        validator.validateRequiredField(request.description(), context);
    }

    private SpecialistService retrieveService(String serviceId, Context context) {
        log.info("[serviceId: {}] Try to find service by id", serviceId);
        Optional<SpecialistService> opt = repository.findById(serviceId);
        if (opt.isEmpty()) {
            context.setError(SPECIALIST_SERVICE_NOT_FOUND);
            return null;
        }
        SpecialistService specialistService = opt.get();
        log.info("[serviceId: {}] Found: {}", serviceId, specialistService);

        return specialistService;
    }

    protected Response errorResponse(Request request, Context context) {
        log.error("[serviceId: {}] Returning error response", request.serviceId());
        return new Response(request.serviceId(), context.isSuccess(), context.getErrorCode().getMessage());
    }

    protected Response successResponse(Request request) {
        log.info("[serviceId: {}] Returning success response", request.serviceId());
        return new Response(request.serviceId(), true, null);
    }
}

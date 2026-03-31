package ru.belov.ourabroad.api.usecases.create.specialistservice.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.belov.ourabroad.api.usecases.create.specialistservice.CreateSpecialistServiceUseCase;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.core.domain.SpecialistServiceFactory;
import ru.belov.ourabroad.poi.storage.SpecialistServiceRepository;
import ru.belov.ourabroad.web.validators.ErrorCode;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateSpecialistServiceUseCaseImpl
        implements CreateSpecialistServiceUseCase {

    private final SpecialistServiceRepository repository;

    @Override
    public Response execute(Request request) {
        Context context = new Context();
        String specialistProfileId = request.specialistProfileId();
        log.info("[specialistProfileId: {}] Start to create service", specialistProfileId);

        validateRequest(request, context);
        if (!context.isSuccess()) {
            log.info("[specialistProfileId: {}] Validation failed", specialistProfileId);
            return errorResponse(specialistProfileId, context);
        }

        SpecialistService service = makeServiceFromRequest(specialistProfileId, request);
        saveSpecialistService(specialistProfileId, service);

        log.info("[specialistProfileId: {}] Service created successfully", specialistProfileId);
        return successResponse(specialistProfileId);
    }

    protected void validateRequest(Request request, Context context) {
        String specialistProfileId = request.specialistProfileId();
        log.info("[specialistProfileId: {}] Validating request", specialistProfileId);

        if (!StringUtils.hasText(request.title())) {
            log.error("[specialistProfileId: {}] Validation failed: title is empty", specialistProfileId);
            context.setError(ErrorCode.VALIDATION_ERROR);
            return;
        }
        if (request.price() == null || request.price() < 0) {
            log.error("[specialistProfileId: {}] Validation failed: price is null or negative", specialistProfileId);
            context.setError(ErrorCode.VALIDATION_ERROR);
            return;
        }
        log.info("[specialistProfileId: {}] Validation success", specialistProfileId);
    }

    private SpecialistService makeServiceFromRequest(String specialistProfileId, Request request) {
        return SpecialistServiceFactory.create(
                specialistProfileId,
                request.title(),
                request.description(),
                request.price(),
                "USD"
        );
    }

    private void saveSpecialistService(String specialistProfileId, SpecialistService service) {
        log.info("[specialistProfileId: {}] Saving specialist service", specialistProfileId);
        repository.save(service);
    }

    protected Response errorResponse(String specialistProfileId, Context context) {
        log.error("[specialistProfileId: {}] Returning error response", specialistProfileId);
        return new Response(specialistProfileId, false, context.getErrorMessage());
    }

    protected Response successResponse(String specialistProfileId) {
        log.info("[specialistProfileId: {}] Returning success response", specialistProfileId);
        return new Response(specialistProfileId, true, ErrorCode.SUCCESS.getMessage());
    }
}
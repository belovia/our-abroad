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


        log.info("[specialistProfileId: {}] Start to create service",
                specialistProfileId);

        validateRequest(specialistProfileId, request, context);

        SpecialistService service =
                makeServiceFromRequest(specialistProfileId, request, context);

        if (!context.isSuccess()) {
            return errorResponse(specialistProfileId, context);
        }

        saveSpecialistService(specialistProfileId, service);


        return successResponse(specialistProfileId, service);
    }

    protected void validateRequest(String specialistProfileId, Request request, Context context) {
        log.info("[specialistProfileId: {}] Validating requestDto", specialistProfileId);

        if (!StringUtils.hasText(request.title())) {
            log.error("[specialistProfileId: {}] Validation failed: title is empty", specialistProfileId);
            context.setError(ErrorCode.VALIDATION_ERROR);
            return;
        }
        if (request.price() == null || request.price() < 0) {
            log.error("[specialistProfileId: {}] Validation failed price is null or less zero", specialistProfileId);
            context.setError(ErrorCode.VALIDATION_ERROR);
            return;
        }
        log.info("[specialistProfileId: {}] Validating success", specialistProfileId);
    }


    private SpecialistService makeServiceFromRequest(String specialistProfileId, Request request, Context context) {
        return SpecialistServiceFactory.create(
                specialistProfileId,
                request.title(),
                request.description(),
                request.price(),
                "USD"
        );
    }

    private void saveSpecialistService(String specialistProfileId, SpecialistService service) {
        log.info("[specialistProfileId: {}] Start saving specialistService", specialistProfileId);
        repository.save(service);
    }

    protected Response errorResponse(String specialistProfileId, Context ctx) {
        log.error("[specialistProfileId: {}] Returning error response", specialistProfileId);
        return new Response(specialistProfileId, ctx.isSuccess(), ctx.getErrorCode().getMessage());
    }

    protected Response successResponse(String specialistProfileId, SpecialistService service) {
        log.info("[specialistProfileId: {}] Returning success response", specialistProfileId);
        return new Response(specialistProfileId, true, null);
    }
}
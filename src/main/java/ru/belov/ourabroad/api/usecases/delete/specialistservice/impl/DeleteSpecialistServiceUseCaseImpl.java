package ru.belov.ourabroad.api.usecases.delete.specialistservice.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.belov.ourabroad.api.usecases.delete.specialistservice.DeleteSpecialistServiceUseCase;
import ru.belov.ourabroad.api.usecases.services.specialistservice.SpecialistServiceService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.web.validators.ErrorCode;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteSpecialistServiceUseCaseImpl implements DeleteSpecialistServiceUseCase {

    private final SpecialistServiceService specialistServiceService;

    @Override
    public Response delete(String serviceId) {
        if (!StringUtils.hasText(serviceId)) {
            log.error("Input id is null or empty");
            return new Response(false, ErrorCode.REQUEST_VALIDATION_ERROR.getMessage());
        }
        log.info("[serviceId: {}] Start to delete service", serviceId);
        Context context = new Context();
        specialistServiceService.deleteById(serviceId, context);
        if (!context.isSuccess()) {
            return new Response(false, context.getErrorMessage());
        }
        log.info("[serviceId: {}] deleted", serviceId);
        return new Response(true, ErrorCode.SUCCESS.getMessage());
    }
}

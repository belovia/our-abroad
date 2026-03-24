package ru.belov.ourabroad.api.usecases.get.specialistservice;

import ru.belov.ourabroad.core.domain.SpecialistService;
@FunctionalInterface
public interface GetSpecialistServiceByServiceIdUseCase {

    Response execute(Request request);

    record Request(String serviceId) {}

    record Response(
            SpecialistService service,
            boolean success,
            String errorMessage
    ) {
    }
}

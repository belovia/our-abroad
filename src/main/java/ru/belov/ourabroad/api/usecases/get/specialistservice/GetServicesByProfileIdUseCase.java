package ru.belov.ourabroad.api.usecases.get.specialistservice;

import ru.belov.ourabroad.core.domain.SpecialistService;

import java.util.Set;

@FunctionalInterface
public interface GetServicesByProfileIdUseCase {

    Response execute(Request request);

    record Request(String specialistProfileId) {}

    record Response(
            Set<SpecialistService> services,
            boolean success,
            String errorMessage
    ) {}

}

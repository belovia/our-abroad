package ru.belov.ourabroad.api.usecases.change.specialistprofile;

import ru.belov.ourabroad.web.dto.change.SpecialistServiceDto;

import java.util.Set;

public interface ChangeSpecialistProfileUseCase {

    Response execute(Request request);

    record Request(
            String profileId,
            String description,
            Set<SpecialistServiceDto> services
    ) {
    }

    record Response(
            String serviceId,
            boolean success,
            String message) {
    }
}

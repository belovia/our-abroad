package ru.belov.ourabroad.api.usecases.change.specialistprofile;

import ru.belov.ourabroad.web.dto.change.SpecialistServiceDto;

import java.util.Set;
@FunctionalInterface
public interface ChangeSpecialistProfileUseCase {

    Response execute(Request request);

    record Request(
            String profileId,
            String description,
            Set<SpecialistServiceDto> services
    ) {
    }

    record Response(
            String profileId,
            boolean success,
            String message) {
    }
}

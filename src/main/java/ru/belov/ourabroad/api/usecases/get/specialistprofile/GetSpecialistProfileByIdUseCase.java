package ru.belov.ourabroad.api.usecases.get.specialistprofile;

import ru.belov.ourabroad.core.domain.SpecialistProfile;
@FunctionalInterface
public interface GetSpecialistProfileByIdUseCase {
    Response execute(Request request);

    record Request(String specialistProfileId) {
    }

    record Response(SpecialistProfile specialist, boolean success,
                    String errorMessage) {
    }
}

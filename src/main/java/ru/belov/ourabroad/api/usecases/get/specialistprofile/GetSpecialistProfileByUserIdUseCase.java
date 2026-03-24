package ru.belov.ourabroad.api.usecases.get.specialistprofile;

import ru.belov.ourabroad.core.domain.SpecialistProfile;

@FunctionalInterface
public interface GetSpecialistProfileByUserIdUseCase {
    Response execute(Request request);

    record Request(String userId) {
    }

    record Response(SpecialistProfile specialist, boolean success,
                    String errorMessage) {
    }
}

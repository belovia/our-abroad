package ru.belov.ourabroad.api.usecases.create.specialistprofile;

public interface CreateSpecialistProfileUseCase {

    Response execute(Request request);

    record Request(String description) {
    }

    record Response(
            String userId,
            boolean success,
            String errorMessage
    ) {
    }
}

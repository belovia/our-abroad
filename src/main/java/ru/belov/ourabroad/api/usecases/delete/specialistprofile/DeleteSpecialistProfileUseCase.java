package ru.belov.ourabroad.api.usecases.delete.specialistprofile;

public interface DeleteSpecialistProfileUseCase {

    Response delete(String specialistProfileId);

    record Response(boolean success, String message) {
    }
}

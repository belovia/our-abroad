package ru.belov.ourabroad.api.usecases.delete.specialistservice;

public interface DeleteSpecialistServiceUseCase {

    Response delete(String serviceId);

    record Response(boolean success, String message) {
    }
}

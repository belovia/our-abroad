package ru.belov.ourabroad.api.usecases.create.specialistservice;

public interface CreateSpecialistServiceUseCase {
    Response execute(Request request);

    public record Request(
            String specialistProfileId,
            String title,
            String description,
            Integer price,
            String currency) {
    }

    public record Response(
            String specialistProfileId,
            boolean success,
            String message
    ) {
    }
}
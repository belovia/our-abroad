package ru.belov.ourabroad.api.usecases.change.specialistservice;

public interface ChangeSpecialistServiceUseCase {
    Response execute(Request request);

    public record Request(
            String serviceId,
            String title,
            Integer price,
            String description
    ) {}

    public record Response(
            String serviceId,
            boolean success,
            String message
    ) {}


}

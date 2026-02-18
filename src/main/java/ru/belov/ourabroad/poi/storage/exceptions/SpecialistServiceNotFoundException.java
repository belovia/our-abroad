package ru.belov.ourabroad.poi.storage.exceptions;

public class SpecialistServiceNotFoundException extends RuntimeException {

    public SpecialistServiceNotFoundException(String serviceId) {
        super("SpecialistService with serviceId " + serviceId + " not found");
    }
}

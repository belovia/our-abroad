package ru.belov.ourabroad.poi.storage.exceptions;

public class SpecialistProfileAlreadyExistsException extends RuntimeException {
    public SpecialistProfileAlreadyExistsException(String specialistProfileId) {
        super("User with specialistProfileId " + specialistProfileId + " already exists");
    }
}

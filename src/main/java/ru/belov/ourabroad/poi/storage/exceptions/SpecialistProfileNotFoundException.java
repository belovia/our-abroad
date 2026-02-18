package ru.belov.ourabroad.poi.storage.exceptions;

public class SpecialistProfileNotFoundException extends RuntimeException {

    public SpecialistProfileNotFoundException(String profileId) {
        super("SpecialistProfile with profileId " + profileId + " not found");
    }
}

package ru.belov.ourabroad.poi.storage.exceptions;

import ru.belov.ourabroad.web.validators.ValidationError;
import ru.belov.ourabroad.web.validators.ValidationResult;

import java.util.List;

public class ValidationException extends RuntimeException {
    private final ValidationResult validationResult;

    public ValidationException(ValidationResult validationResult) {
        super(validationResult.getCombinedErrorMessage());
        this.validationResult = validationResult;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    public List<ValidationError> getErrors() {
        return validationResult.getErrors();
    }
}
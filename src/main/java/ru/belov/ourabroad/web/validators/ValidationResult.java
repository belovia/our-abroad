package ru.belov.ourabroad.web.validators;

import ru.belov.ourabroad.poi.storage.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ValidationResult {

    private final List<ValidationError> errors = new ArrayList<>();

    public boolean isValid() {
        return errors.isEmpty();
    }

    public void addError(ValidationError error) {
        errors.add(error);
    }

    public void addErrors(Collection<ValidationError> errors) {
        this.errors.addAll(errors);
    }

    public void merge(ValidationResult other) {
        if (other != null && !other.isValid()) {
            this.errors.addAll(other.errors);
        }
    }

    public List<ValidationError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public List<String> getErrorMessages() {
        return errors.stream()
                .map(ValidationError::getMessage)
                .toList();
    }

    public String getCombinedErrorMessage() {
        return String.join("; ", getErrorMessages());
    }

    public void throwIfInvalid() {
        if (!isValid()) {
            throw new ValidationException(this);
        }
    }


    public static ValidationResult ok() {
        return new ValidationResult();
    }

    public static ValidationResult withError(ValidationError error) {
        ValidationResult result = new ValidationResult();
        result.addError(error);
        return result;
    }
}
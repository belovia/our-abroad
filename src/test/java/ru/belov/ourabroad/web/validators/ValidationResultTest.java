package ru.belov.ourabroad.web.validators;

import org.junit.jupiter.api.Test;
import ru.belov.ourabroad.poi.storage.exceptions.ValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.belov.ourabroad.web.validators.ValidationError.EMAIL_REQUIRED;
import static ru.belov.ourabroad.web.validators.ValidationError.PASSWORD_REQUIRED;

class ValidationResultTest {

    @Test
    void WHEN_noErrors_THEN_isValidTrue() {
        ValidationResult result = ValidationResult.ok();

        assertTrue(result.isValid());
    }

    @Test
    void WHEN_hasErrors_THEN_isValidFalse() {
        ValidationResult result = ValidationResult.ok();
        result.addError(EMAIL_REQUIRED);

        assertFalse(result.isValid());
    }

    @Test
    void WHEN_addError_THEN_errorAdded() {
        ValidationResult result = ValidationResult.ok();

        result.addError(EMAIL_REQUIRED);

        assertEquals(1, result.getErrors().size());
        assertEquals(EMAIL_REQUIRED, result.getErrors().get(0));
    }


    @Test
    void WHEN_addErrors_THEN_allErrorsAdded() {
        ValidationResult result = ValidationResult.ok();

        result.addErrors(List.of(EMAIL_REQUIRED, PASSWORD_REQUIRED));

        assertEquals(2, result.getErrors().size());
        assertTrue(result.getErrors().contains(EMAIL_REQUIRED));
        assertTrue(result.getErrors().contains(PASSWORD_REQUIRED));
    }


    @Test
    void WHEN_mergeValidResult_THEN_noErrorsAdded() {
        ValidationResult target = ValidationResult.ok();
        ValidationResult source = ValidationResult.ok();

        target.merge(source);

        assertTrue(target.isValid());
        assertTrue(target.getErrors().isEmpty());
    }

    @Test
    void WHEN_mergeInvalidResult_THEN_errorsMerged() {
        ValidationResult target = ValidationResult.ok();
        ValidationResult source = ValidationResult.ok();
        source.addError(EMAIL_REQUIRED);

        target.merge(source);

        assertFalse(target.isValid());
        assertEquals(1, target.getErrors().size());
        assertEquals(EMAIL_REQUIRED, target.getErrors().get(0));
    }

    @Test
    void WHEN_mergeNull_THEN_noErrorsAdded() {
        ValidationResult target = ValidationResult.ok();

        target.merge(null);

        assertTrue(target.isValid());
    }

    @Test
    void WHEN_getErrors_THEN_listIsUnmodifiable() {
        ValidationResult result = ValidationResult.ok();
        result.addError(EMAIL_REQUIRED);

        List<ValidationError> errors = result.getErrors();

        assertThrows(UnsupportedOperationException.class,
                () -> errors.add(PASSWORD_REQUIRED));
    }

    @Test
    void WHEN_getErrorMessages_THEN_returnMessages() {
        ValidationResult result = ValidationResult.ok();
        result.addError(EMAIL_REQUIRED);
        result.addError(PASSWORD_REQUIRED);

        List<String> messages = result.getErrorMessages();

        assertEquals(List.of(EMAIL_REQUIRED.getMessage(), PASSWORD_REQUIRED.getMessage()), messages);
    }

    @Test
    void WHEN_noErrors_THEN_combinedMessageIsEmpty() {
        ValidationResult result = ValidationResult.ok();

        assertEquals("", result.getCombinedErrorMessage());
    }

    @Test
    void WHEN_valid_THEN_throwIfInvalidDoesNothing() {
        ValidationResult result = ValidationResult.ok();

        assertDoesNotThrow(result::throwIfInvalid);
    }

    @Test
    void WHEN_invalid_THEN_throwIfInvalidThrowsException() {
        ValidationResult result = ValidationResult.ok();
        result.addError(EMAIL_REQUIRED);

        ValidationException exception =
                assertThrows(ValidationException.class, result::throwIfInvalid);

        assertEquals(result, exception.getValidationResult());
    }

    @Test
    void WHEN_ok_THEN_validResult() {
        ValidationResult result = ValidationResult.ok();

        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void WHEN_withError_THEN_invalidResultWithError() {
        ValidationResult result = ValidationResult.withError(EMAIL_REQUIRED);

        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertEquals(EMAIL_REQUIRED, result.getErrors().get(0));
    }

}
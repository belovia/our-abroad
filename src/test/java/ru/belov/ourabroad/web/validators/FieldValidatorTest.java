package ru.belov.ourabroad.web.validators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.core.domain.Context;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        FieldValidator.class
})
class FieldValidatorTest {

    @Autowired
    private FieldValidator validator;

    @Test
    void contextCreated() {
        assertNotNull(validator);
    }

    @Test
    void WHEN_validateRequiredField_stringFieldIsValid_THEN_contextRemainsSuccessful() {
        // Arrange
        Context context = new Context();
        String validField = "valid value";

        // Action
        validator.validateRequiredField(validField, context);

        // Asserts
        assertThat(context.isSuccess()).isTrue();
        assertThat(context.getErrorCode()).isNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t", "\n"})
    void WHEN_validateRequiredField_stringFieldIsInvalid_THEN_setFieldRequiredError(String invalidField) {
        // Arrange
        Context context = new Context();

        // Action
        validator.validateRequiredField(invalidField, context);

        // Asserts
        assertThat(context.isSuccess()).isFalse();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.FIELD_REQUIRED);
    }

    @Test
    void WHEN_validateRequiredField_stringFieldIsValidButContextAlreadyHasError_THEN_skipValidation() {
        // Arrange
        Context context = new Context();
        context.setError(ErrorCode.REQUEST_VALIDATION_ERROR);
        String validField = "valid value";

        // Action
        validator.validateRequiredField(validField, context);

        // Asserts
        assertThat(context.isSuccess()).isFalse();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.REQUEST_VALIDATION_ERROR);
    }

    @Test
    void WHEN_validateRequiredField_stringFieldIsInvalidButContextAlreadyHasError_THEN_keepOriginalError() {
        // Arrange
        Context context = new Context();
        context.setError(ErrorCode.REQUEST_VALIDATION_ERROR);
        String invalidField = null;

        // Action
        validator.validateRequiredField(invalidField, context);

        // Asserts
        assertThat(context.isSuccess()).isFalse();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.REQUEST_VALIDATION_ERROR);
        assertThat(context.getErrorCode()).isNotEqualTo(ErrorCode.FIELD_REQUIRED);
    }

    @Test
    void WHEN_validateRequiredField_integerFieldIsValid_THEN_contextRemainsSuccessful() {
        // Arrange
        Context context = new Context();
        Integer validField = 123;

        // Action
        validator.validateRequiredField(validField, context);

        // Asserts
        assertThat(context.isSuccess()).isTrue();
        assertThat(context.getErrorCode()).isNull();
    }

    @Test
    void WHEN_validateRequiredField_integerFieldIsNull_THEN_setFieldRequiredError() {
        // Arrange
        Context context = new Context();
        Integer invalidField = null;

        // Action
        validator.validateRequiredField(invalidField, context);

        // Asserts
        assertThat(context.isSuccess()).isFalse();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.FIELD_REQUIRED);
    }

    @Test
    void WHEN_validateRequiredField_integerFieldIsNullButContextAlreadyHasError_THEN_skipValidation() {
        // Arrange
        Context context = new Context();
        context.setError(ErrorCode.REQUEST_VALIDATION_ERROR);
        Integer invalidField = null;

        // Action
        validator.validateRequiredField(invalidField, context);

        // Asserts
        assertThat(context.isSuccess()).isFalse();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.REQUEST_VALIDATION_ERROR);
        assertThat(context.getErrorCode()).isNotEqualTo(ErrorCode.FIELD_REQUIRED);
    }

    @Test
    void WHEN_validateRequiredField_integerFieldIsValidButContextAlreadyHasError_THEN_skipValidation() {
        // Arrange
        Context context = new Context();
        context.setError(ErrorCode.REQUEST_VALIDATION_ERROR);
        Integer validField = 456;

        // Action
        validator.validateRequiredField(validField, context);

        // Asserts
        assertThat(context.isSuccess()).isFalse();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.REQUEST_VALIDATION_ERROR);
    }

    @Test
    void WHEN_validateRequest_requestIsValid_THEN_contextRemainsSuccessful() {
        // Arrange
        Context context = new Context();
        Object validRequest = new Object();

        // Action
        validator.validateRequest(validRequest, context);

        // Asserts
        assertThat(context.isSuccess()).isTrue();
        assertThat(context.getErrorCode()).isNull();
    }

    @Test
    void WHEN_validateRequest_requestIsNull_THEN_setRequestValidationError() {
        // Arrange
        Context context = new Context();
        Object invalidRequest = null;

        // Action
        validator.validateRequest(invalidRequest, context);

        // Asserts
        assertThat(context.isSuccess()).isFalse();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.REQUEST_VALIDATION_ERROR);
    }

    @Test
    void WHEN_validateRequest_requestIsNullButContextAlreadyHasError_THEN_keepOriginalError() {
        // Arrange
        Context context = new Context();
        context.setError(ErrorCode.FIELD_REQUIRED);
        Object invalidRequest = null;

        // Action
        validator.validateRequest(invalidRequest, context);

        // Asserts
        assertThat(context.isSuccess()).isFalse();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.FIELD_REQUIRED);
        assertThat(context.getErrorCode()).isNotEqualTo(ErrorCode.REQUEST_VALIDATION_ERROR);
    }

    @Test
    void WHEN_validateRequest_requestIsValidButContextAlreadyHasError_THEN_keepOriginalError() {
        // Arrange
        Context context = new Context();
        context.setError(ErrorCode.FIELD_REQUIRED);
        Object validRequest = new Object();

        // Action
        validator.validateRequest(validRequest, context);

        // Asserts
        assertThat(context.isSuccess()).isFalse();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.FIELD_REQUIRED);
    }

    @Test
    void WHEN_multipleValidationsCalledWithSuccessContext_THEN_allValidationsPass() {
        // Arrange
        Context context = new Context();
        String validString = "test";
        Integer validInteger = 42;
        Object validRequest = new Object();

        // Action
        validator.validateRequiredField(validString, context);
        validator.validateRequiredField(validInteger, context);
        validator.validateRequest(validRequest, context);

        // Asserts
        assertThat(context.isSuccess()).isTrue();
        assertThat(context.getErrorCode()).isNull();
    }

    @Test
    void WHEN_firstValidationFails_THEN_subsequentValidationsAreSkipped() {
        // Arrange
        Context context = new Context();
        String invalidString = null;
        Integer validInteger = 42;
        Object validRequest = new Object();

        // Action
        validator.validateRequiredField(invalidString, context);
        validator.validateRequiredField(validInteger, context);
        validator.validateRequest(validRequest, context);

        // Asserts
        assertThat(context.isSuccess()).isFalse();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.FIELD_REQUIRED);
    }

    @Test
    void WHEN_validateRequiredField_calledWithEmptyStringAndNullInteger_THEN_firstErrorPersists() {
        // Arrange
        Context context = new Context();
        String emptyString = "";
        Integer nullInteger = null;

        // Action
        validator.validateRequiredField(emptyString, context);
        validator.validateRequiredField(nullInteger, context);

        // Asserts
        assertThat(context.isSuccess()).isFalse();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.FIELD_REQUIRED);
    }

    @ParameterizedTest
    @ValueSource(strings = {"valid", "123", "test@email.com", "a", "very long string with multiple words and special characters!@#$%^&*()"})
    void WHEN_validateRequiredField_calledWithVariousValidStrings_THEN_contextRemainsSuccessful(String validString) {
        // Arrange
        Context context = new Context();

        // Action
        validator.validateRequiredField(validString, context);

        // Asserts
        assertThat(context.isSuccess()).isTrue();
        assertThat(context.getErrorCode()).isNull();
    }

    @Test
    void WHEN_validateRequiredField_calledWithZeroInteger_THEN_contextRemainsSuccessful() {
        // Arrange
        Context context = new Context();
        Integer zeroValue = 0;

        // Action
        validator.validateRequiredField(zeroValue, context);

        // Asserts
        assertThat(context.isSuccess()).isTrue();
        assertThat(context.getErrorCode()).isNull();
    }

    @Test
    void WHEN_validateRequiredField_calledWithNegativeInteger_THEN_contextRemainsSuccessful() {
        // Arrange
        Context context = new Context();
        Integer negativeValue = -100;

        // Action
        validator.validateRequiredField(negativeValue, context);

        // Asserts
        assertThat(context.isSuccess()).isTrue();
        assertThat(context.getErrorCode()).isNull();
    }

}
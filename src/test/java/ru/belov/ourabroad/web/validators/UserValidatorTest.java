package ru.belov.ourabroad.web.validators;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.web.dto.create.CreateUserRequest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        UserValidator.class
})
class UserValidatorTest {

    @Autowired
    private UserValidator validator;

    @Test
    void WHEN_userValidator_validateCreateUserRequest_invalidPassword_THEN_returnFalse() {

        // Arrange
        CreateUserRequest request = CreateUserRequest.builder()
                .email("testUserEmail@gmail.com")
                .password("testPassword")
                .phone("123456789")
                .build();

        ValidationResult validationResult = validator.validateCreateUserRequest(request);

        assertFalse(validationResult.isValid());
    }

    @Test
    void WHEN_userValidator_validateCreateUserRequest_validPassword_THEN_returnTrue() {

        CreateUserRequest request = CreateUserRequest.builder()
                .email("testUserEmail@gmail.com")
                .password("testPassword12")
                .phone("+79999999999")
                .build();

        ValidationResult validationResult = validator.validateCreateUserRequest(request);

        assertTrue(validationResult.isValid());
    }

    @Test
    void WHEN_emailIsNull_THEN_returnFalse() {
        CreateUserRequest request = validRequest()
                .email(null)
                .build();

        ValidationResult result = validator.validateCreateUserRequest(request);

        assertFalse(result.isValid());
    }

    @Test
    void WHEN_emailIsEmpty_THEN_returnFalse() {
        CreateUserRequest request = validRequest()
                .email("")
                .build();

        ValidationResult result = validator.validateCreateUserRequest(request);

        assertFalse(result.isValid());
    }

    @Test
    void WHEN_emailHasInvalidFormat_THEN_returnFalse() {
        CreateUserRequest request = validRequest()
                .email("invalid-email")
                .build();

        ValidationResult result = validator.validateCreateUserRequest(request);

        assertFalse(result.isValid());
    }


    @Test
    void WHEN_phoneHasInvalidFormat_THEN_returnFalse() {
        CreateUserRequest request = validRequest()
                .phone("123456789")
                .build();

        ValidationResult result = validator.validateCreateUserRequest(request);

        assertFalse(result.isValid());
    }

    @Test
    void WHEN_phoneIsValid_THEN_returnTrue() {
        CreateUserRequest request = validRequest()
                .phone("+79999999999")
                .build();

        ValidationResult result = validator.validateCreateUserRequest(request);

        assertTrue(result.isValid());
    }

    @Test
    void WHEN_phoneIsEmpty_THEN_returnFalse() {
        CreateUserRequest request = validRequest()
                .phone("")
                .build();

        ValidationResult result = validator.validateCreateUserRequest(request);

        assertTrue(result.isValid());
    }


    @Test
    void WHEN_passwordIsNull_THEN_returnFalse() {
        CreateUserRequest request = validRequest()
                .password(null)
                .build();

        ValidationResult result = validator.validateCreateUserRequest(request);

        assertFalse(result.isValid());
    }

    @Test
    void WHEN_passwordIsTooShort_THEN_returnFalse() {
        CreateUserRequest request = validRequest()
                .password("A1abc")
                .build();

        ValidationResult result = validator.validateCreateUserRequest(request);

        assertFalse(result.isValid());
    }

    @Test
    void WHEN_passwordIsTooLong_THEN_returnFalse() {
        CreateUserRequest request = validRequest()
                .password("A1abcdefghijklmno")
                .build();

        ValidationResult result = validator.validateCreateUserRequest(request);

        assertFalse(result.isValid());
    }

    @Test
    void WHEN_passwordWithoutUppercase_THEN_returnFalse() {
        CreateUserRequest request = validRequest()
                .password("password12")
                .build();

        ValidationResult result = validator.validateCreateUserRequest(request);

        assertFalse(result.isValid());
    }

    @Test
    void WHEN_passwordWithoutDigit_THEN_returnFalse() {
        CreateUserRequest request = validRequest()
                .password("Password")
                .build();

        ValidationResult result = validator.validateCreateUserRequest(request);

        assertFalse(result.isValid());
    }

    @Test
    void WHEN_passwordIsValid_THEN_returnTrue() {
        CreateUserRequest request = validRequest()
                .password("Password12")
                .build();

        ValidationResult result = validator.validateCreateUserRequest(request);

        assertTrue(result.isValid());
    }


    private CreateUserRequest.CreateUserRequestBuilder validRequest() {
        return CreateUserRequest.builder()
                .email("test@gmail.com")
                .password("Password12")
                .phone("+79999999999");
    }
}
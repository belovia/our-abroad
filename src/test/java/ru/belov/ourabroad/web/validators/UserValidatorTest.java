package ru.belov.ourabroad.web.validators;


import org.junit.jupiter.api.Test;
import ru.belov.ourabroad.web.dto.CreateUserRequest;

import static org.junit.jupiter.api.Assertions.assertFalse;

class UserValidatorTest {

    @Test
    void WHEN_userValidator_validateCreateUserRequest_THEN_returnFalse() {

        // Arrange
        CreateUserRequest request = CreateUserRequest.builder()
                .email("testUserEmail@gmail.com")
                .password("testPassword")
                .phone("123456789")
                .build();
        assertFalse(UserValidator.validateCreateUserRequest(request));
    }
}
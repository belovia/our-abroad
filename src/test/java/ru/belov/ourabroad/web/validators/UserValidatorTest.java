package ru.belov.ourabroad.web.validators;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.create.user.CreateUserUseCase.Request;
import ru.belov.ourabroad.core.domain.Context;

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
    void WHEN_inputRequestIsValid_THEN_contextIsSuccessTrue() {
        // Arrange
        Request request = validRequest();
        Context context = new Context();

        //Action
        validator.validateCreateUserRequest(request,context);

        // Assert
        assertTrue(context.isSuccess());
    }

    @Test
    void WHEN_inputInvalidRequest_THEN_contextIsSuccessFalse() {
        // Arrange
        Request request = new Request(
                "test@gmail.com",
                "89999999990",
                "Password12",
                "@testTelegramUsername",
                "@testWhatsappUsername",
                "developer");
        Context context = new Context();

        //Action
        validator.validateCreateUserRequest(request,context);

        // Assert
        assertFalse(context.isSuccess());
    }

    @Test
    void WHEN_inputInvalidRequestEmail_THEN_contextIsSuccessFalse() {
        // Arrange
        Request request = new Request(
                "testEmail",
                "+79999999990",
                "Password12",
                "@testTelegramUsername",
                "@testWhatsappUsername",
                "developer");
        Context context = new Context();

        //Action
        validator.validateCreateUserRequest(request,context);

        // Assert
        assertFalse(context.isSuccess());
    }

    @Test
    void WHEN_inputInvalidRequestNullEmail_THEN_contextIsSuccessFalse() {
        // Arrange
        Request request = new Request(
                null,
                "+79999999990",
                "Password12",
                "@testTelegramUsername",
                "@testWhatsappUsername",
                "developer");
        Context context = new Context();

        //Action
        validator.validateCreateUserRequest(request,context);

        // Assert
        assertFalse(context.isSuccess());
    }

    @Test
    void WHEN_inputInvalidRequestNullPhone_THEN_contextIsSuccessFalse() {
        // Arrange
        Request request = new Request(
                "test@Email",
                null,
                "Password12",
                "@testTelegramUsername",
                "@testWhatsappUsername",
                "developer");
        Context context = new Context();

        //Action
        validator.validateCreateUserRequest(request,context);

        // Assert
        assertFalse(context.isSuccess());
    }

    @Test
    void WHEN_inputInvalidRequestNullPassword_THEN_contextIsSuccessFalse() {
        // Arrange
        Request request = new Request(
                "test@Email",
                "+79999999990",
                null,
                "@testTelegramUsername",
                "@testWhatsappUsername",
                "developer");
        Context context = new Context();

        //Action
        validator.validateCreateUserRequest(request,context);

        // Assert
        assertFalse(context.isSuccess());
    }

    private Request validRequest() {
        return new Request(
                "test@gmail.com",
                "+79999999999",
                "Password12",
                "@testTelegramUsername",
                "@testWhatsappUsername",
                "developer");
    }

}
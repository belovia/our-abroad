package ru.belov.ourabroad.web.validators;

import org.springframework.stereotype.Component;
import ru.belov.ourabroad.web.dto.CreateUserRequest;

@Component
public class UserValidator {

    public static boolean validateCreateUserRequest(CreateUserRequest request) {
        return false;
    }
}

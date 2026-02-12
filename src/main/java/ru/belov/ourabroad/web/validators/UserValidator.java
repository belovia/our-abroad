package ru.belov.ourabroad.web.validators;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.belov.ourabroad.web.dto.create.CreateUserRequest;

import java.util.regex.Pattern;

import static ru.belov.ourabroad.web.validators.ValidationError.*;

@Component
public class UserValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z\\d+_.-]+@[A-Za-z\\d.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+\\d{10,15}$");


    public ValidationResult validateCreateUserRequest(CreateUserRequest request) {
        ValidationResult result = new ValidationResult();

        result.merge(validateEmail(request.getEmail()));
        result.merge(validatePhone(request.getPhone()));
        result.merge(validatePassword(request.getPassword()));

        return result;
    }

    public boolean isValidCreateUserRequest(CreateUserRequest request) {
        return validateCreateUserRequest(request).isValid();
    }

    public ValidationResult validateEmail(String email) {
        ValidationResult result = new ValidationResult();

        if (!StringUtils.hasText(email)) {
            result.addError(EMAIL_REQUIRED);
            return result;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            result.addError(EMAIL_INVALID_FORMAT);
        }

        return result;
    }

    public ValidationResult validatePhone(String phone) {
        ValidationResult result = new ValidationResult();

        if (StringUtils.hasText(phone) && !PHONE_PATTERN.matcher(phone).matches()) {
            result.addError(PHONE_INVALID_FORMAT);
        }

        return result;
    }

    public ValidationResult validatePassword(String password) {
        ValidationResult result = new ValidationResult();

        if (!StringUtils.hasText(password)) {
            result.addError(PASSWORD_REQUIRED);
            return result;
        }

        return result;
    }

    public ValidationResult validateTelegram(String username) {
        ValidationResult result = ValidationResult.ok();

        if (StringUtils.hasText(username)) {
            if (!username.matches("^@\\w{5,32}$")) {
                result.addError(TELEGRAM_INVALID);
            }
        }
        return result;
    }

    public ValidationResult validateWhatsapp(String phone) {
        ValidationResult result = ValidationResult.ok();

        if (StringUtils.hasText(phone)) {
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                result.addError(WHATSAPP_INVALID);
            }
        }
        return result;
    }
}

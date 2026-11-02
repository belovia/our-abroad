package ru.belov.ourabroad.web.validators;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.belov.ourabroad.core.domain.Context;

import java.util.regex.Pattern;

import static ru.belov.ourabroad.api.usecases.create.user.CreateUserUseCase.Request;
import static ru.belov.ourabroad.web.validators.ErrorCode.*;

@Component
public class UserValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z\\d+_.-]+@[A-Za-z\\d.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+\\d{10,15}$");

    private static final Pattern PASSWORD_ALLOWED_CHARS =
            Pattern.compile("^[A-Za-z\\d@$!%*?&]+$");

    private static final Pattern PASSWORD_HAS_UPPERCASE =
            Pattern.compile(".*[A-Z].*");

    private static final Pattern PASSWORD_HAS_DIGIT =
            Pattern.compile(".*\\d.*");

    public void validateCreateUserRequest(Request request, Context context) {
        validateEmail(request.email(), context);
        validatePhone(request.phone(), context);
        validatePassword(request.password(), context);
    }

    public void validateId(String id, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        if (!StringUtils.hasText(id)) {
            context.setError(USER_ID_REQUIRED);
        }
    }

    public void validateEmail(String email, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        if (!StringUtils.hasText(email)) {
            context.setError(EMAIL_REQUIRED);
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            context.setError(EMAIL_INVALID_FORMAT);
        }
    }

    public void validatePhone(String phone, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        if (StringUtils.hasText(phone) && !PHONE_PATTERN.matcher(phone).matches()) {
            context.setError(PHONE_INVALID_FORMAT);
        }
    }

    public void validatePassword(String password, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        if (!StringUtils.hasText(password)) {
            context.setError(PASSWORD_REQUIRED);
            return;
        }
        if (password.length() < 8 || password.length() > 16) {
            context.setError(PASSWORD_LENGTH_INVALID);
            return;
        }
        if (!PASSWORD_ALLOWED_CHARS.matcher(password).matches()) {
            context.setError(PASSWORD_INVALID_CHARACTERS);
            return;
        }
        if (!PASSWORD_HAS_UPPERCASE.matcher(password).matches()) {
            context.setError(PASSWORD_UPPERCASE_REQUIRED);
            return;
        }
        if (!PASSWORD_HAS_DIGIT.matcher(password).matches()) {
            context.setError(PASSWORD_DIGIT_REQUIRED);
        }
    }

    public void validateTelegram(String username, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        if (StringUtils.hasText(username)) {
            if (!username.matches("^@\\w{5,32}$")) {
                context.setError(TELEGRAM_INVALID);
            }
        }
    }

    public void validateDisplayName(String displayName, Context context) {
        if (!context.isSuccess() || displayName == null) {
            return;
        }
        if (displayName.length() < 2) {
            context.setError(DISPLAY_NAME_TOO_SHORT);
            return;
        }
        if (displayName.length() > 50) {
            context.setError(DISPLAY_NAME_TOO_LONG);
        }
    }

    public void validateBio(String bio, Context context) {
        if (!context.isSuccess() || bio == null) {
            return;
        }
        if (bio.length() > 500) {
            context.setError(BIO_TOO_LONG);
        }
    }

    public void validateWhatsapp(String phone, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        if (StringUtils.hasText(phone)) {
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                context.setError(WHATSAPP_INVALID);
            }
        }
    }
}

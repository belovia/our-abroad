package ru.belov.ourabroad.web.validators;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.belov.ourabroad.core.domain.Context;

import static ru.belov.ourabroad.web.validators.ErrorCode.FIELD_REQUIRED;
import static ru.belov.ourabroad.web.validators.ErrorCode.REQUEST_VALIDATION_ERROR;

@Component
public class FieldValidator {

    public void validateRequiredField(String field, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        if (!StringUtils.hasText(field)) {
            context.setError(FIELD_REQUIRED);
        }
    }

    public void validateRequest(Object request, Context context) {
        if (request == null) {
            context.setError(REQUEST_VALIDATION_ERROR);
        }
    }

    public void validateRequiredField(Integer field, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        if (field == null) {
            context.setError(FIELD_REQUIRED);
        }
    }
}

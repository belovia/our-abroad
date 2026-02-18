package ru.belov.ourabroad.web.validators;

import org.springframework.util.StringUtils;

public class IdValidator {

    private IdValidator() {}

    public static void requireUserId(String userId) {
        if (!StringUtils.hasText(userId)) {
            throw new IllegalArgumentException("userId is empty");
        }
    }
}

package ru.belov.ourabroad.poi.storage.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class ParamHelper {
    public void putParam(Map<String, Object> params,
                         String name,
                         Object value,
                         String userId) {

        if (value == null) {
            if (userId != null) {
                log.warn("[user_id: {}] null sql param '{}'", userId, name);
            } else {
                log.warn("[user_id: unknown] null sql param '{}'", name);
            }
        }

        params.put(name, value);
    }

    public void putParam(Map<String, Object> params,
                         String name,
                         Object value) {

        if (value == null) {
            log.error("[parameterName: {} ] parameter value is null", name);
        }

        params.put(name, value);
    }

    public void requireUserId(String userId, String action) {
        if (userId == null) {
            log.error(
                    "[user_id: null] userId is null for action: {}",
                    action
            );
            throw new IllegalArgumentException("userId must not be null");
        }
    }
}

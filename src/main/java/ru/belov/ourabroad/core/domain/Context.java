package ru.belov.ourabroad.core.domain;

import lombok.Getter;
import ru.belov.ourabroad.web.validators.ErrorCode;

@Getter
public class Context {

    private boolean success;
    private ErrorCode errorCode;

    public Context() {
        this.success = true;
    }

    public void setError(ErrorCode errorCode) {
        if (!success) {
            return;
        }
        this.success = false;
        this.errorCode = errorCode;
    }
}

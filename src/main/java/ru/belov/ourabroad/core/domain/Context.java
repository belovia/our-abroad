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

    /**
     * Marks the flow as successful and sets {@link ErrorCode#SUCCESS} for API messages.
     */
    public void setSuccessResult() {
        this.success = true;
        this.errorCode = ErrorCode.SUCCESS;
    }

    public void setError(ErrorCode errorCode) {
        if (!success) {
            return;
        }
        this.success = false;
        this.errorCode = errorCode;
    }

    /**
     * Returns the error message when context is in error state, or {@code null} on success.
     * Prefer this over {@code getErrorCode().getMessage()} to avoid NullPointerException.
     */
    public String getErrorMessage() {
        return errorCode != null ? errorCode.getMessage() : null;
    }
}

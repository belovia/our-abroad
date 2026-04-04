package ru.belov.ourabroad.api.usecases.create.verification;

import ru.belov.ourabroad.core.enums.VerificationType;

public interface CreateVerificationUseCase {

    Response execute(Request request);

    record Request(VerificationType type, String relatedEntityId) {
    }

    record Response(String verificationId, boolean success, String errorMessage) {
    }
}

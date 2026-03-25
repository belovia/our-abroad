package ru.belov.ourabroad.api.usecases.get.verification;

import ru.belov.ourabroad.core.domain.Verification;

public interface GetVerificationByIdUseCase {

    Response execute(Request request);

    record Request(String verificationId) {
    }

    record Response(Verification verification, boolean success, String errorMessage) {
    }
}

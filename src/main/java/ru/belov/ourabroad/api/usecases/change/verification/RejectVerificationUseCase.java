package ru.belov.ourabroad.api.usecases.change.verification;

public interface RejectVerificationUseCase {

    Response execute(Request request);

    record Request(String verificationId) {
    }

    record Response(String verificationId, boolean success, String errorMessage) {
    }
}

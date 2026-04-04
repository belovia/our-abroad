package ru.belov.ourabroad.api.usecases.get.verification;

import ru.belov.ourabroad.core.domain.Verification;

import java.util.List;

public interface GetVerificationsByUserIdUseCase {

    Response execute(Request request);

    record Request() {
    }

    record Response(List<Verification> verifications, boolean success, String errorMessage) {
    }
}

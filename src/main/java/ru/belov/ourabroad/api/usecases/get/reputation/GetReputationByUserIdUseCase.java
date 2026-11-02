package ru.belov.ourabroad.api.usecases.get.reputation;

import ru.belov.ourabroad.core.domain.Reputation;

public interface GetReputationByUserIdUseCase {

    Response execute(Request request);

    record Request() {
    }

    record Response(
            Reputation reputation,
            boolean success,
            String errorMessage
    ) {
    }
}

package ru.belov.ourabroad.api.usecases.get.user;

import ru.belov.ourabroad.core.domain.User;

@FunctionalInterface
public interface GetUserByIdUsecase {

    Response execute(Request request);

    record Request(
            String userId) {
    }

    record Response(
            User user,
            boolean success,
            String errorMessage
    ) {
    }
}

package ru.belov.ourabroad.api.usecases.get.user;

import ru.belov.ourabroad.core.domain.User;
@FunctionalInterface
public interface GetUserByEmailUseCase {

    Response execute(Request request);

    record Request(
            String email
    ) {
    }

    record Response(
            User user,
            boolean success,
            String errorMessage
    ) {
    }


}

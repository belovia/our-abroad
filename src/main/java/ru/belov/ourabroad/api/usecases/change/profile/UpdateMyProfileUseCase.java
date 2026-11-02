package ru.belov.ourabroad.api.usecases.change.profile;

import java.util.Set;

public interface UpdateMyProfileUseCase {

    Response execute(Request request);

    record Request(
            String displayName,
            String avatarUrl,
            String bio,
            String country,
            String city,
            Set<String> languages
    ) {
    }

    record Response(
            boolean success,
            String message,
            String userId,
            String displayName,
            String avatarUrl,
            String bio,
            String country,
            String city,
            Set<String> languages
    ) {
    }
}

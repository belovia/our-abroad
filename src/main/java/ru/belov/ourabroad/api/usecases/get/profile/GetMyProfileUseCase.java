package ru.belov.ourabroad.api.usecases.get.profile;

import java.util.Set;

public interface GetMyProfileUseCase {

    Response execute();

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

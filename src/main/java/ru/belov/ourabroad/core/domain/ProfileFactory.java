package ru.belov.ourabroad.core.domain;

import ru.belov.ourabroad.core.domain.Profile;

import java.util.Set;

public final class ProfileFactory {

    private ProfileFactory() {}

    public static Profile createEmpty(String userId) {
        return Profile.create(
                userId,
                null,
                null,
                null,
                null,
                null,
                Set.of()
        );
    }

    public static Profile create(
            String userId,
            String displayName,
            String country,
            String city,
            Set<String> languages
    ) {
        return Profile.create(
                userId,
                displayName,
                null,
                null,
                country,
                city,
                languages
        );
    }

    public static Profile fromDb(
            String userId,
            String displayName,
            String avatarUrl,
            String bio,
            String country,
            String city,
            Set<String> languages
    ) {
        return Profile.create(
                userId,
                displayName,
                avatarUrl,
                bio,
                country,
                city,
                languages
        );
    }
}


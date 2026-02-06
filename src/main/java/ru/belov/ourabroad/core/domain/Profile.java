package ru.belov.ourabroad.core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
public class Profile {

    private final String userId;
    private String displayName;
    private String avatarUrl;
    private String bio;
    private String country;
    private String city;
    private Set<String> languages;

    private Profile(String userId) {
        this.userId = userId;
    }

    public static Profile create(
            String userId,
            String displayName,
            String avatarUrl,
            String bio,
            String country,
            String city,
            Set<String> languages
    ) {
        Objects.requireNonNull(userId, "userId must not be null");

        Profile profile = new Profile(userId);
        profile.update(
                displayName,
                avatarUrl,
                bio,
                country,
                city,
                languages);

        return profile;
    }

    public void update(
            String displayName,
            String avatarUrl,
            String bio,
            String country,
            String city,
            Set<String> languages
    ) {
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.country = country;
        this.city = city;
        this.languages = languages;
    }
}

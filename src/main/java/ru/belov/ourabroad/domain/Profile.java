package ru.belov.ourabroad.domain;

import lombok.Getter;

import java.util.Set;

@Getter
public class Profile {
    private final String userId;
    private String displayName;
    private String avatarUrl;
    private String bio;
    private String country;
    private String city;
    private Set<String> languages;


    public Profile(String userId) {
        this.userId = userId;
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

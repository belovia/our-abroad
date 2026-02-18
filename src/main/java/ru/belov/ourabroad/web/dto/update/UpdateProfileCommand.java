package ru.belov.ourabroad.web.dto.update;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class UpdateProfileCommand {
    private String userId;
    private String displayName;
    private String avatarUrl;
    private String bio;
    private String country;
    private String city;
    private Set<String> languages;
}
package ru.belov.ourabroad.web.dto;

import lombok.Getter;

import java.util.Set;

@Getter
public class UpdateProfileRequest {
    private String userId;
    private String displayName;
    private String avatarUrl;
    private String bio;
    private String country;
    private String city;
    private Set<String> languages;
}
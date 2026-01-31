package ru.belov.ourabroad.poi.storage;

import ru.belov.ourabroad.domain.Profile;

import java.util.Optional;

public interface ProfileRepository {

    Optional<Profile> findByUserId(String userId);

    void save(Profile profile);

    boolean update(Profile profile);
}

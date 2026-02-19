package ru.belov.ourabroad.poi.storage;

import ru.belov.ourabroad.core.domain.SpecialistProfile;

import java.util.Optional;
public interface SpecialistProfileRepository {

    Optional<SpecialistProfile> findById(String id);

    Optional<SpecialistProfile> findByUserId(String userId);

    void save(SpecialistProfile profile);

    boolean update(SpecialistProfile profile);

    boolean deleteById(String specialistProfileId);
}

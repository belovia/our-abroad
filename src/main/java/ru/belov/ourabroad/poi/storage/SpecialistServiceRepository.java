package ru.belov.ourabroad.poi.storage;

import ru.belov.ourabroad.core.domain.SpecialistService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SpecialistServiceRepository {

    Optional<SpecialistService> findById(String id);

    Set<SpecialistService> findBySpecialistProfileId(String specialistId);

    void save(SpecialistService service);

    boolean update(SpecialistService service);

    boolean deleteById(String id);

    boolean existsById(String id);
}

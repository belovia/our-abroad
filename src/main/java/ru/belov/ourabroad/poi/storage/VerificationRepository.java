package ru.belov.ourabroad.poi.storage;

import ru.belov.ourabroad.core.domain.Verification;
import ru.belov.ourabroad.core.enums.VerificationType;

import java.util.List;
import java.util.Optional;

public interface VerificationRepository {

    Optional<Verification> findById(String id);

    List<Verification> findByUserId(String userId);

    Optional<Verification> findPendingByUserTypeAndRelated(
            String userId,
            VerificationType type,
            String relatedEntityId
    );

    void save(Verification verification);

    boolean updateStatus(Verification verification);
}

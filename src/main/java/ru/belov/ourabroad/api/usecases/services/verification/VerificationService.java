package ru.belov.ourabroad.api.usecases.services.verification;

import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Verification;
import ru.belov.ourabroad.core.enums.VerificationType;

import java.util.List;

public interface VerificationService {

    Verification findById(String id, Context context);

    List<Verification> findByUserId(String userId, Context context);

    void save(Verification verification, Context context);

    void updateStatus(Verification verification, Context context);

    boolean hasPendingDuplicate(String userId, VerificationType type, String relatedEntityId);
}

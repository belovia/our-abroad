package ru.belov.ourabroad.api.usecases.services.verification.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.api.usecases.services.verification.VerificationService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Verification;
import ru.belov.ourabroad.core.enums.VerificationType;
import ru.belov.ourabroad.poi.storage.VerificationRepository;

import java.util.List;
import java.util.Optional;

import static ru.belov.ourabroad.web.validators.ErrorCode.VERIFICATION_NOT_FOUND;

@Component
@RequiredArgsConstructor
@Slf4j
public class VerificationServiceImpl implements VerificationService {

    private final VerificationRepository repository;

    @Override
    public Verification findById(String id, Context context) {
        log.info("[verificationId: {}] Try to find verification", id);
        Optional<Verification> fromDb = repository.findById(id);
        if (fromDb.isEmpty()) {
            log.warn("[verificationId: {}] Verification not found", id);
            context.setError(VERIFICATION_NOT_FOUND);
            return null;
        }
        return fromDb.get();
    }

    @Override
    public List<Verification> findByUserId(String userId, Context context) {
        log.info("[userId: {}] Listing verifications", userId);
        return repository.findByUserId(userId);
    }

    @Override
    public void save(Verification verification, Context context) {
        log.info("[verificationId: {}] Saving verification", verification.getId());
        repository.save(verification);
    }

    @Override
    public void updateStatus(Verification verification, Context context) {
        log.info("[verificationId: {}] Updating verification status", verification.getId());
        repository.updateStatus(verification);
    }

    @Override
    public boolean hasPendingDuplicate(String userId, VerificationType type, String relatedEntityId) {
        return repository.findPendingByUserTypeAndRelated(userId, type, relatedEntityId).isPresent();
    }
}

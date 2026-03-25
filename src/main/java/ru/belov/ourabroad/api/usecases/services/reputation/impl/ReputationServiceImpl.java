package ru.belov.ourabroad.api.usecases.services.reputation.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.api.usecases.services.reputation.ReputationService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Reputation;
import ru.belov.ourabroad.poi.storage.ReputationRepository;

import java.util.Optional;

import static ru.belov.ourabroad.web.validators.ErrorCode.REPUTATION_NOT_FOUND;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReputationServiceImpl implements ReputationService {

    private final ReputationRepository repository;

    @Override
    public boolean existsByUserId(String userId) {
        return repository.findByUserId(userId).isPresent();
    }

    @Override
    public Reputation findByUserId(String userId, Context context) {
        log.info("[userId: {}] Try to find reputation", userId);
        Optional<Reputation> fromDb = repository.findByUserId(userId);
        if (fromDb.isEmpty()) {
            log.warn("[userId: {}] Reputation not found", userId);
            context.setError(REPUTATION_NOT_FOUND);
            return null;
        }
        return fromDb.get();
    }

    @Override
    public void save(Reputation reputation, Context context) {
        log.info("[userId: {}] Saving reputation", reputation.getUserId());
        repository.save(reputation);
    }

    @Override
    public void update(Reputation reputation, Context context) {
        log.info("[userId: {}] Updating reputation", reputation.getUserId());
        repository.update(reputation);
    }

    @Override
    public void addPoints(String userId, int pointsDelta, Context context) {
        if (!context.isSuccess()) {
            return;
        }
        if (pointsDelta == 0) {
            log.info("[userId: {}] Reputation delta is zero, skip", userId);
            return;
        }
        log.info("[userId: {}] Apply reputation delta {}", userId, pointsDelta);
        Optional<Reputation> fromDb = repository.findByUserId(userId);
        if (fromDb.isEmpty()) {
            log.warn("[userId: {}] Reputation not found for delta", userId);
            context.setError(REPUTATION_NOT_FOUND);
            return;
        }
        Reputation reputation = fromDb.get();
        reputation.applyScoreDelta(pointsDelta);
        repository.update(reputation);
    }
}

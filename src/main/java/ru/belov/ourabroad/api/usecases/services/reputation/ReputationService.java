package ru.belov.ourabroad.api.usecases.services.reputation;

import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Reputation;

public interface ReputationService {

    boolean existsByUserId(String userId);

    Reputation findByUserId(String userId, Context context);

    void save(Reputation reputation, Context context);

    void update(Reputation reputation, Context context);
}

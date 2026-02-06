package ru.belov.ourabroad.poi.storage;

import ru.belov.ourabroad.core.domain.Reputation;

import java.util.Optional;

public interface ReputationRepository {

    Optional<Reputation> findByUserId(String userId);

    void save(Reputation reputation);

    boolean update(Reputation reputation);
}
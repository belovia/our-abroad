package ru.belov.ourabroad.poi.storage;

import ru.belov.ourabroad.core.domain.Vote;

import java.util.Optional;

public interface VoteRepository {

    Optional<Vote> findByUserIdAndEntityId(String userId, String entityId);

    void save(Vote vote);

    void updateType(Vote vote);

    void deleteById(String voteId);
}

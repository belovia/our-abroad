package ru.belov.ourabroad.poi.storage;

import ru.belov.ourabroad.core.domain.Vote;

import java.util.Optional;

public interface VoteRepository {

    Optional<Vote> findByUserIdAndEntityId(String userId, String entityId);

    boolean save(Vote vote);

    boolean updateType(Vote vote);

    void deleteById(String voteId);
}

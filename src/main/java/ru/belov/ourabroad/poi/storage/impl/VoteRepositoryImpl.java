package ru.belov.ourabroad.poi.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.belov.ourabroad.core.domain.Vote;
import ru.belov.ourabroad.poi.storage.VoteRepository;
import ru.belov.ourabroad.poi.storage.mappers.QaVoteRowMapper;
import ru.belov.ourabroad.poi.storage.sql.QaVoteSql;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VoteRepositoryImpl implements VoteRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final QaVoteRowMapper rowMapper;

    @Override
    public Optional<Vote> findByUserIdAndEntityId(String userId, String entityId) {
        Map<String, Object> params = Map.of("userId", userId, "entityId", entityId);
        return jdbc.query(QaVoteSql.FIND_BY_USER_AND_ENTITY, params, rowMapper).stream().findFirst();
    }

    @Override
    public boolean save(Vote vote) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", vote.getId());
        params.put("userId", vote.getUserId());
        params.put("entityId", vote.getEntityId());
        params.put("voteType", vote.getType().name());
        return jdbc.update(QaVoteSql.INSERT, params) > 0;
    }

    @Override
    public boolean updateType(Vote vote) {
        return save(vote);
    }

    @Override
    public void deleteById(String voteId) {
        jdbc.update(QaVoteSql.DELETE_BY_ID, Map.of("id", voteId));
    }
}

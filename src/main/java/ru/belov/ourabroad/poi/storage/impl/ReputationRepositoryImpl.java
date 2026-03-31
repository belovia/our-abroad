package ru.belov.ourabroad.poi.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.belov.ourabroad.core.domain.Reputation;
import ru.belov.ourabroad.poi.storage.ReputationRepository;
import ru.belov.ourabroad.poi.storage.mappers.ReputationRowMapper;
import ru.belov.ourabroad.poi.storage.sql.ReputationSql;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReputationRepositoryImpl implements ReputationRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ReputationRowMapper rowMapper;

    @Override
    public Optional<Reputation> findByUserId(String userId) {
        return jdbcTemplate.query(
                ReputationSql.FIND_BY_USER_ID,
                Map.of("userId", userId),
                rowMapper
        ).stream().findFirst();
    }

    @Override
    public void save(Reputation reputation) {
        Map<String, Object> params = new HashMap<>();

        put(params, "userId", reputation.getUserId());
        put(params, "score", reputation.getScore());
        put(params, "level", reputation.getLevel());

        jdbcTemplate.update(ReputationSql.UPSERT, params);
    }

    @Override
    public boolean update(Reputation reputation) {
        save(reputation);
        return true;
    }

    private void put(Map<String, Object> params, String key, Object value) {
        if (value == null) {
            log.warn("[user_id: {}] null value for param '{}'",
                    params.get("userId"), key);
        }
        params.put(key, value);
    }
}

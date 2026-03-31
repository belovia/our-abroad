package ru.belov.ourabroad.poi.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.belov.ourabroad.core.domain.Verification;
import ru.belov.ourabroad.core.enums.VerificationType;
import ru.belov.ourabroad.poi.storage.VerificationRepository;
import ru.belov.ourabroad.poi.storage.mappers.VerificationRowMapper;
import ru.belov.ourabroad.poi.storage.sql.VerificationSql;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VerificationRepositoryImpl implements VerificationRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final VerificationRowMapper rowMapper;

    @Override
    public Optional<Verification> findById(String id) {
        return jdbc.query(
                VerificationSql.FIND_BY_ID,
                Map.of("id", id),
                rowMapper
        ).stream().findFirst();
    }

    @Override
    public List<Verification> findByUserId(String userId) {
        return jdbc.query(
                VerificationSql.FIND_BY_USER_ID,
                Map.of("userId", userId),
                rowMapper
        );
    }

    @Override
    public Optional<Verification> findPendingByUserTypeAndRelated(
            String userId,
            VerificationType type,
            String relatedEntityId
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("type", type.name());
        params.put("relatedEntityId", relatedEntityId);
        return jdbc.query(
                VerificationSql.FIND_PENDING_BY_USER_TYPE_AND_RELATED,
                params,
                rowMapper
        ).stream().findFirst();
    }

    @Override
    public void save(Verification verification) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", verification.getId());
        params.put("userId", verification.getUserId());
        params.put("type", verification.getType().name());
        params.put("relatedEntityId", verification.getRelatedEntityId());
        params.put("status", verification.getStatus().name());
        params.put("createdAt", Timestamp.valueOf(verification.getCreatedAt()));
        params.put(
                "verifiedAt",
                verification.getVerifiedAt() != null
                        ? Timestamp.valueOf(verification.getVerifiedAt())
                        : null
        );
        jdbc.update(VerificationSql.INSERT, params);
    }

    @Override
    public boolean updateStatus(Verification verification) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", verification.getId());
        params.put("status", verification.getStatus().name());
        params.put(
                "verifiedAt",
                verification.getVerifiedAt() != null
                        ? Timestamp.valueOf(verification.getVerifiedAt())
                        : null
        );
        return jdbc.update(VerificationSql.UPDATE_STATUS, params) > 0;
    }
}

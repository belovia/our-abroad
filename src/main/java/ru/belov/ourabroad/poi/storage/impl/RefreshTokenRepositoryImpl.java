package ru.belov.ourabroad.poi.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.belov.ourabroad.core.domain.RefreshToken;
import ru.belov.ourabroad.poi.storage.RefreshTokenRepository;
import ru.belov.ourabroad.poi.storage.mappers.RefreshTokenRowMapper;
import ru.belov.ourabroad.poi.storage.sql.RefreshTokenSql;

import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final RefreshTokenRowMapper rowMapper;

    @Override
    public void save(RefreshToken token) {
        jdbc.update(
                RefreshTokenSql.INSERT,
                Map.of(
                        "id", token.getId(),
                        "userId", token.getUserId(),
                        "tokenHash", token.getTokenHash(),
                        "expiresAt", token.getExpiresAt(),
                        "createdAt", token.getCreatedAt(),
                        "deviceInfo", token.getDeviceInfo() != null ? token.getDeviceInfo() : ""
                )
        );
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return jdbc.query(
                RefreshTokenSql.FIND_BY_HASH,
                Map.of("tokenHash", tokenHash),
                rowMapper
        ).stream().findFirst();
    }

    @Override
    public void deleteById(String id) {
        jdbc.update(RefreshTokenSql.DELETE_BY_ID, Map.of("id", id));
    }

    @Override
    public void deleteByTokenHash(String tokenHash) {
        jdbc.update(RefreshTokenSql.DELETE_BY_HASH, Map.of("tokenHash", tokenHash));
    }

    @Override
    public void deleteAllByUserId(String userId) {
        jdbc.update(RefreshTokenSql.DELETE_ALL_BY_USER, Map.of("userId", userId));
    }
}

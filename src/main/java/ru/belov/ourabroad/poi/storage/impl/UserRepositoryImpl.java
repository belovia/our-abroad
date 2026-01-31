package ru.belov.ourabroad.poi.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.belov.ourabroad.database.mappers.UserRowMapper;
import ru.belov.ourabroad.domain.User;
import ru.belov.ourabroad.enums.UserStatus;
import ru.belov.ourabroad.poi.storage.UserRepository;
import ru.belov.ourabroad.poi.storage.helper.ParamHelper;
import ru.belov.ourabroad.poi.storage.sql.UserSql;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final UserRowMapper rowMapper;
    private final ParamHelper paramHelper;

    @Override
    public Optional<User> findById(String userId) {
        Map<String, Object> params = new HashMap<>();
        paramHelper.putParam(params, "userId", userId, userId);
        return jdbcTemplate.query(
                UserSql.FIND_BY_ID,
                params,
                rowMapper
        ).stream().findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email, String userId) {

        Map<String, Object> params = new HashMap<>();
        paramHelper.putParam(params, "email", email, userId);
        return jdbcTemplate.query(
                UserSql.FIND_BY_EMAIL,
                Map.of("email", email),
                rowMapper
        ).stream().findFirst();
    }

    @Override
    public void save(User user) {

        Map<String, Object> params = new HashMap<>();

        paramHelper.putParam(params, "id", user.getId(), user.getId());
        paramHelper.putParam(params, "email", user.getEmail(), user.getId());
        paramHelper.putParam(params, "phone", user.getPhone(), user.getId());
        paramHelper.putParam(params, "passwordHash", user.getPassword(), user.getId());
        paramHelper.putParam(params, "status", user.getStatus().name(), user.getId());
        paramHelper.putParam(params, "createdAt", user.getCreatedAt(), user.getId());
        paramHelper.putParam(params, "lastLoginAt", user.getLastLoginAt(), user.getId());

        jdbcTemplate.update(
                UserSql.INSERT,
                params
        );
    }

    @Override
    public boolean updateLastLogin(String userId, LocalDateTime lastLoginAt) {

        Map<String, Object> params = new HashMap<>();
        paramHelper.putParam(params, "id", userId, userId);
        paramHelper.putParam(params, "lastLoginAt", lastLoginAt, userId);

        return jdbcTemplate.update(
                UserSql.UPDATE_LAST_LOGIN,
                params
        ) > 0;
    }

    @Override
    public boolean updateStatus(String userId, UserStatus status) {
        Map<String, Object> params = new HashMap<>();
        paramHelper.putParam(params, "id", userId, userId);
        paramHelper.putParam(params, "status", status, userId);

        return jdbcTemplate.update(
                UserSql.UPDATE_STATUS,
                params
        ) > 0;
    }
}

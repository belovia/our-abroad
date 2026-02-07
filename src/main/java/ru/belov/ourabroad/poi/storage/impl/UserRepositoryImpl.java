package ru.belov.ourabroad.poi.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.core.enums.UserStatus;
import ru.belov.ourabroad.poi.storage.UserRepository;
import ru.belov.ourabroad.poi.storage.helper.ParamHelper;
import ru.belov.ourabroad.poi.storage.mappers.UserRowMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static ru.belov.ourabroad.poi.storage.sql.UserSql.*;

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
        paramHelper.putParam(params, "id", userId, userId);
        log.info("[userId: {}] Prepare to find user by id: {}", userId, userId);

        return jdbcTemplate.query(
                FIND_BY_ID,
                params,
                rowMapper
        ).stream().findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {

        Map<String, Object> params = new HashMap<>();
        paramHelper.putParam(params, "email", email);
        return jdbcTemplate.query(
                FIND_BY_EMAIL,
                params,
                rowMapper
        ).stream().findFirst();
    }

    @Override
    public void save(User user) {

        Map<String, Object> params = new HashMap<>();
        log.info("[userId: {}] Prepare user to saving", user.getId());
        paramHelper.putParam(params, "id", user.getId(), user.getId());
        paramHelper.putParam(params, "email", user.getEmail(), user.getId());
        paramHelper.putParam(params, "phone", user.getPhone(), user.getId());
        paramHelper.putParam(params, "passwordHash", user.getPassword(), user.getId());
        paramHelper.putParam(params, "status", user.getStatus().name(), user.getId());
        paramHelper.putParam(params, "createdAt", user.getCreatedAt(), user.getId());
        paramHelper.putParam(params, "lastLoginAt", user.getLastLoginAt(), user.getId());

        jdbcTemplate.update(
                INSERT,
                params
        );
    }

    @Override
    public boolean updateLastLogin(String userId, LocalDateTime lastLoginAt) {

        Map<String, Object> params = new HashMap<>();
        log.info("[userId: {}] Prepare user to update login at", userId);

        paramHelper.putParam(params, "id", userId, userId);
        paramHelper.putParam(params, "lastLoginAt", lastLoginAt, userId);
        log.info("[userId: {}] Update user last login at", userId);
        return jdbcTemplate.update(
                UPDATE_LAST_LOGIN,
                params
        ) > 0;
    }

    @Override
    public boolean updateStatus(String userId, UserStatus status) {
        Map<String, Object> params = new HashMap<>();
        log.info("[userId: {}] Prepare user to update status", userId);
        paramHelper.putParam(params, "id", userId, userId);
        paramHelper.putParam(params, "status", status, userId);

        log.info("[userId: {}] Update user status. New status: {}", userId, status);
        return jdbcTemplate.update(
                UPDATE_STATUS,
                params
        ) > 0;
    }

    @Override
    public boolean existsByEmail(String email) {

        Map<String, Object> params = new HashMap<>();
        paramHelper.putParam(params, "email", email);
        return jdbcTemplate.query(
                FIND_BY_EMAIL,
                params,
                rowMapper
        ).stream().findFirst().isPresent();
    }
}

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

import static ru.belov.ourabroad.core.domain.NameSpaces.*;
import static ru.belov.ourabroad.poi.storage.sql.UserSql.FIND_BY_EMAIL;
import static ru.belov.ourabroad.poi.storage.sql.UserSql.FIND_BY_ID;
import static ru.belov.ourabroad.poi.storage.sql.UserSql.UPDATE_LAST_LOGIN;
import static ru.belov.ourabroad.poi.storage.sql.UserSql.UPDATE_STATUS;
import static ru.belov.ourabroad.poi.storage.sql.UserSql.UPSERT;

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
        paramHelper.putParam(params, ID, userId, userId);
        log.info("[userId: {}] Prepare to find user by id: {}", userId, userId);

        return jdbcTemplate.query(
                FIND_BY_ID,
                params,
                rowMapper
        ).stream().findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        log.info("[userId: {}] Prepare to find user by id: {}", email);
        Map<String, Object> params = new HashMap<>();
        paramHelper.putParam(params, EMAIL, email);
        return jdbcTemplate.query(
                FIND_BY_EMAIL,
                params,
                rowMapper
        ).stream().findFirst();
    }

    @Override
    public void save(User user) {

        if (user == null) {
            log.error("User is null, cancel saving");
            return;
        }

        log.info("[userId: {}] Try to save user", user.getId());

        Map<String, Object> params = new HashMap<>();

        log.info("[userId: {}] Prepare user to saving", user.getId());

        paramHelper.putParam(params, ID, user.getId(), user.getId());
        paramHelper.putParam(params, EMAIL, user.getEmail(), user.getId());
        paramHelper.putParam(params, PHONE, user.getPhone(), user.getId());
        paramHelper.putParam(params, PASSWORD_HASH, user.getPassword(), user.getId());
        paramHelper.putParam(params, TELEGRAM_USERNAME, user.getTelegramUsername(), user.getId());
        paramHelper.putParam(params, WHATSAPP_USERNAME, user.getWhatsappNumber(), user.getId());
        paramHelper.putParam(params, ACTIVITY, user.getActivity(), user.getId());
        paramHelper.putParam(params, ROLES, user.getRoles(), user.getId());
        paramHelper.putParam(params, STATUS, user.getStatus().name(), user.getId());
        paramHelper.putParam(params, CREATED_AT, user.getCreatedAt(), user.getId());
        paramHelper.putParam(params, LAST_LOGIN_AT, user.getLastLoginAt(), user.getId());

        log.info("[userId: {}] Save user", user.getId());

        Boolean inserted = jdbcTemplate.queryForObject(UPSERT, params, Boolean.class);
        if (Boolean.TRUE.equals(inserted)) {
            log.info("[userId: {}] User upsert: INSERT", user.getId());
        } else {
            log.info("[userId: {}] User upsert: UPDATE", user.getId());
        }
    }

    @Override
    public boolean updateLastLogin(String userId, LocalDateTime lastLoginAt) {

        Map<String, Object> params = new HashMap<>();
        log.info("[userId: {}] Prepare user to update login at", userId);

        paramHelper.putParam(params, ID, userId, userId);
        paramHelper.putParam(params, LAST_LOGIN_AT, lastLoginAt, userId);
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
        paramHelper.putParam(params, ID, userId, userId);
        paramHelper.putParam(params, STATUS, status, userId);

        log.info("[userId: {}] Update user status. New status: {}", userId, status);
        return jdbcTemplate.update(
                UPDATE_STATUS,
                params
        ) > 0;
    }

    @Override
    public boolean existsByEmail(String userId, String email) {
        log.info("[userId: {}] Start checking exist email", userId);
        Map<String, Object> params = new HashMap<>();
        paramHelper.putParam(params, EMAIL, email);
        return jdbcTemplate.query(
                FIND_BY_EMAIL,
                params,
                rowMapper
        ).stream().findFirst().isPresent();
    }
}

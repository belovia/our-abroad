package ru.belov.ourabroad.poi.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.belov.ourabroad.poi.storage.mappers.ProfileRowMapper;
import ru.belov.ourabroad.core.domain.Profile;
import ru.belov.ourabroad.poi.storage.ProfileRepository;
import ru.belov.ourabroad.poi.storage.helper.ParamHelper;
import ru.belov.ourabroad.poi.storage.sql.ProfileSql;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProfileRepositoryImpl implements ProfileRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ProfileRowMapper rowMapper;
    private final ParamHelper paramHelper;

    @Override
    public Optional<Profile> findByUserId(String userId) {
        return jdbcTemplate.query(
                ProfileSql.FIND_BY_USER_ID,
                Map.of("userId", userId),
                rowMapper
        ).stream().findFirst();
    }

    @Override
    public void save(Profile profile) {

        Map<String, Object> params = new HashMap<>();
        fillCommonParams(profile, params);

        jdbcTemplate.update(
                ProfileSql.INSERT,
                params
        );
    }

    @Override
    public boolean update(Profile profile) {

        Map<String, Object> params = new HashMap<>();
        fillCommonParams(profile, params);

        return jdbcTemplate.update(
                ProfileSql.UPDATE,
                params
        ) > 0;
    }

    protected void fillCommonParams(Profile profile, Map<String, Object> params) {
        paramHelper.putParam(params, "userId", profile.getUserId(), profile.getUserId());
        paramHelper.putParam(params, "displayName", profile.getDisplayName(), profile.getUserId());
        paramHelper.putParam(params, "avatarUrl", profile.getAvatarUrl(), profile.getUserId());
        paramHelper.putParam(params, "bio", profile.getBio(), profile.getUserId());
        paramHelper.putParam(params, "country", profile.getCountry(), profile.getUserId());
        paramHelper.putParam(params, "city", profile.getCity(), profile.getUserId());
        paramHelper.putParam(params, "languages", String.join(",", profile.getLanguages()), profile.getUserId());

    }
}

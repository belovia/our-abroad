package ru.belov.ourabroad.poi.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.belov.ourabroad.poi.storage.mappers.SpecialistProfileRowMapper;
import ru.belov.ourabroad.core.domain.SpecialistProfile;
import ru.belov.ourabroad.poi.storage.SpecialistProfileRepository;
import ru.belov.ourabroad.poi.storage.helper.ParamHelper;
import ru.belov.ourabroad.poi.storage.sql.SpecialistProfileSql;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SpecialistProfileRepositoryImpl implements SpecialistProfileRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SpecialistProfileRowMapper rowMapper;
    private final ParamHelper paramHelper;


    @Override
    public Optional<SpecialistProfile> findById(String id) {
        Map<String, Object> params = new HashMap<>();
        paramHelper.putParam(params, "id", id, id);
        return jdbcTemplate.query(
                SpecialistProfileSql.FIND_BY_ID,
                params,
                rowMapper
        ).stream().findFirst();
    }

    @Override
    public Optional<SpecialistProfile> findByUserId(String userId) {
        Map<String, Object> params = new HashMap<>();
        paramHelper.putParam(params, "userId", userId, userId);
        return jdbcTemplate.query(
                SpecialistProfileSql.FIND_BY_USER_ID,
                params,
                rowMapper
        ).stream().findFirst();
    }

    @Override
    public void save(SpecialistProfile profile) {

        Map<String, Object> params = new HashMap<>();
        paramHelper.putParam(params, "id", profile.getId(), profile.getUserId());
        paramHelper.putParam(params, "userId", profile.getUserId(), profile.getUserId());
        putCommonParams(profile, params);

        jdbcTemplate.update(
                SpecialistProfileSql.INSERT,
                params);
    }

    @Override
    public boolean update(SpecialistProfile profile) {

        Map<String, Object> params = new HashMap<>();
        paramHelper.putParam(params, "id", profile.getId(), profile.getUserId());
        putCommonParams(profile, params);

        return jdbcTemplate.update(
                SpecialistProfileSql.UPDATE,
                params

        ) > 0;
    }

    private void putCommonParams(SpecialistProfile profile, Map<String, Object> params) {
        paramHelper.putParam(params, "category", profile.getCategory(), profile.getUserId());
        paramHelper.putParam(params, "description", profile.getDescription(), profile.getUserId());
        paramHelper.putParam(params, "priceFrom", profile.getPriceFrom(), profile.getUserId());
        paramHelper.putParam(params, "priceTo", profile.getPriceTo(), profile.getUserId());
        paramHelper.putParam(params, "active", profile.isActive(), profile.getUserId());
        paramHelper.putParam(params, "rating", profile.getRating(), profile.getUserId());
        paramHelper.putParam(params, "reviewsCount", profile.getReviewsCount(), profile.getUserId());
    }
}

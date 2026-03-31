package ru.belov.ourabroad.poi.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.poi.storage.SpecialistServiceRepository;
import ru.belov.ourabroad.poi.storage.mappers.SpecialistServiceRowMapper;
import ru.belov.ourabroad.poi.storage.sql.SpecialistServiceSql;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SpecialistServiceRepositoryImpl implements SpecialistServiceRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final SpecialistServiceRowMapper rowMapper;

    @Override
    public Optional<SpecialistService> findById(String id) {
        var params = Map.of("id", id);

        return jdbc.query(
                SpecialistServiceSql.FIND_BY_ID,
                params,
                rowMapper
        ).stream().findFirst();
    }

    @Override
    public Set<SpecialistService> findBySpecialistProfileId(String specialistId) {
        return new HashSet<>(jdbc.query(
                SpecialistServiceSql.FIND_BY_SPECIALIST_ID,
                Map.of("specialistId", specialistId),
                rowMapper
        ));
    }

    @Override
    public void save(SpecialistService service) {
        jdbc.update(
                SpecialistServiceSql.INSERT,
                toParams(service)
        );
    }

    @Override
    public boolean update(SpecialistService service) {
        save(service);
        return true;
    }

    @Override
    public boolean deleteById(String id) {
        return jdbc.update(
                SpecialistServiceSql.DELETE,
                Map.of("id", id)
        ) > 0;
    }

    @Override
    public boolean existsById(String id) {
        Boolean result = jdbc.queryForObject(
                SpecialistServiceSql.EXISTS_BY_ID,
                Map.of("id", id),
                Boolean.class
        );
        return Boolean.TRUE.equals(result);
    }

    private Map<String, Object> toParams(SpecialistService s) {
        return Map.of(
                "id", s.getId(),
                "specialistId", s.getSpecialistId(),
                "title", s.getTitle(),
                "description", s.getDescription(),
                "price", s.getPrice(),
                "currency", s.getCurrency(),
                "active", s.isActive()
        );
    }
}
package ru.belov.ourabroad.poi.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.belov.ourabroad.core.domain.SpecialistService;
import ru.belov.ourabroad.core.domain.SpecialistServiceFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SpecialistServiceRowMapper implements RowMapper<SpecialistService> {

    @Override
    public SpecialistService mapRow(ResultSet rs, int rowNum) throws SQLException {
        return SpecialistServiceFactory.fromDb(
                rs.getString("id"),
                rs.getString("specialist_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getInt("price"),
                rs.getString("currency"),
                rs.getBoolean("active")
        );
    }
}

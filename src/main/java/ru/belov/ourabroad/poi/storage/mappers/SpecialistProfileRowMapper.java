package ru.belov.ourabroad.poi.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.core.domain.SpecialistProfile;
import ru.belov.ourabroad.core.domain.SpecialistProfileFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
@Component
public class SpecialistProfileRowMapper implements RowMapper<SpecialistProfile> {


    @Override
    public SpecialistProfile mapRow(ResultSet rs, int rowNum) throws SQLException {

        return SpecialistProfileFactory.fromDb(
                rs.getString("id"),
                rs.getString("user_id"),
                rs.getString("category"),
                rs.getString("description"),
                rs.getObject("price_from", Integer.class),
                rs.getObject("price_to", Integer.class),
                rs.getBoolean("active"),
                rs.getDouble("rating"),
                rs.getInt("reviews_count"));
    }
}

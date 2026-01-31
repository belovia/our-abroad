package ru.belov.ourabroad.database.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.belov.ourabroad.domain.SpecialistProfile;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SpecialistProfileRowMapper implements RowMapper<SpecialistProfile> {


    @Override
    public SpecialistProfile mapRow(ResultSet rs, int rowNum) throws SQLException {
        SpecialistProfile profile = new SpecialistProfile(
                rs.getString("id"),
                rs.getString("user_id")
        );

        profile.update(
                rs.getString("category"),
                rs.getString("description"),
                rs.getInt("price_from"),
                rs.getInt("price_to")
        );

        if (!rs.getBoolean("active")) {
            profile.deactivate();
        }

        profile.updateStats(
                rs.getDouble("rating"),
                rs.getInt("reviews_count")
        );


        return profile;
    }
}

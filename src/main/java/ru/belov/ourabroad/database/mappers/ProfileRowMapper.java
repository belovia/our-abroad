package ru.belov.ourabroad.database.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.belov.ourabroad.domain.Profile;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class ProfileRowMapper implements RowMapper<Profile> {

    private ObjectMapper objectMapper;


    @Override
    public Profile mapRow(ResultSet rs, int rowNum) throws SQLException {
        Profile profile = new Profile(rs.getString("user_id"));


        profile.update(
                rs.getString("display_name"),
                rs.getString("avatar_url"),
                rs.getString("bio"),
                rs.getString("country"),
                rs.getString("city"),
                readLanguages(rs)
        );


        return profile;
    }

    private Set<String> readLanguages(ResultSet rs) throws SQLException {
        String json = rs.getString("languages");
        if (json == null || json.isBlank()) {
            return Set.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new IllegalStateException("Invalid languages JSON", e);
        }
    }
}

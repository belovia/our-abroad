package ru.belov.ourabroad.poi.storage.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.core.domain.Profile;
import ru.belov.ourabroad.core.domain.ProfileFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProfileRowMapper implements RowMapper<Profile> {

    @Override
    public Profile mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ProfileFactory.fromDb(
                rs.getString("user_id"),
                rs.getString("display_name"),
                rs.getString("avatar_url"),
                rs.getString("bio"),
                rs.getString("country"),
                rs.getString("city"),
                readLanguages(rs));
    }

    private Set<String> readLanguages(ResultSet rs) throws SQLException {
        String raw = rs.getString("languages");

        if (raw == null || raw.isBlank()) {
            return Set.of();
        }

        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
    }
}

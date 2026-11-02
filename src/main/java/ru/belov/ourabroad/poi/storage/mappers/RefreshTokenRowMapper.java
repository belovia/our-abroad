package ru.belov.ourabroad.poi.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.core.domain.RefreshToken;
import ru.belov.ourabroad.core.domain.RefreshTokenFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class RefreshTokenRowMapper implements RowMapper<RefreshToken> {

    @Override
    public RefreshToken mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp exp = rs.getTimestamp("expires_at");
        Timestamp cr = rs.getTimestamp("created_at");
        return RefreshTokenFactory.fromDb(
                rs.getString("id"),
                rs.getString("user_id"),
                rs.getString("token_hash"),
                exp != null ? exp.toLocalDateTime() : null,
                cr != null ? cr.toLocalDateTime() : null,
                rs.getString("device_info")
        );
    }
}

package ru.belov.ourabroad.poi.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.core.domain.User;
import ru.belov.ourabroad.core.enums.UserStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.restore(
                rs.getString("id"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("password_hash"),
                UserStatus.valueOf(rs.getString("status")),
                toLocalDateTime(rs, "created_at"),
                toLocalDateTime(rs, "last_login_at")
        );
    }

    private LocalDateTime toLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return ts != null ? ts.toLocalDateTime() : null;
    }
}

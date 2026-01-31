package ru.belov.ourabroad.database.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.belov.ourabroad.domain.User;
import ru.belov.ourabroad.enums.UserStatus;

import javax.swing.tree.TreePath;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getString("id"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("password"),
                UserStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("created_at").toLocalDateTime(),
                toLocalDateTime(rs, "last_login_at")
        );
    }


    private LocalDateTime toLocalDateTime(ResultSet rs, String column) throws SQLException {
        return rs.getTimestamp(column) != null
                ? rs.getTimestamp(column).toLocalDateTime()
                : null;
    }
}

package ru.belov.ourabroad.poi.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.core.domain.Comment;
import ru.belov.ourabroad.core.enums.CommentEntityType;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CommentRowMapper implements RowMapper<Comment> {

    @Override
    public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
        String parentId = rs.getString("parent_id");
        if (rs.wasNull()) {
            parentId = null;
        }
        return Comment.create(
                rs.getString("id"),
                rs.getString("author_id"),
                rs.getString("entity_id"),
                CommentEntityType.valueOf(rs.getString("entity_type")),
                parentId,
                rs.getString("content"),
                rs.getInt("likes"),
                rs.getInt("replies_count"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}

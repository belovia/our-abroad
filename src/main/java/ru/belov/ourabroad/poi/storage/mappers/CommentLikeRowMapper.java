package ru.belov.ourabroad.poi.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.core.domain.CommentLike;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CommentLikeRowMapper implements RowMapper<CommentLike> {

    @Override
    public CommentLike mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CommentLike(
                rs.getString("id"),
                rs.getString("user_id"),
                rs.getString("comment_id")
        );
    }
}

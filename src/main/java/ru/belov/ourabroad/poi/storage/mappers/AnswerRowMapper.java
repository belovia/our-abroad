package ru.belov.ourabroad.poi.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.core.domain.Answer;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AnswerRowMapper implements RowMapper<Answer> {

    @Override
    public Answer mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Answer.create(
                rs.getString("id"),
                rs.getString("question_id"),
                rs.getString("author_id"),
                rs.getString("specialist_profile_id"),
                rs.getString("content"),
                rs.getInt("votes"),
                rs.getBoolean("accepted"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}

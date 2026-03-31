package ru.belov.ourabroad.poi.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.core.domain.Question;
import ru.belov.ourabroad.poi.storage.helper.QaTagsHelper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class QuestionRowMapper implements RowMapper<Question> {

    @Override
    public Question mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Question.create(
                rs.getString("id"),
                rs.getString("author_id"),
                rs.getString("title"),
                rs.getString("content"),
                QaTagsHelper.deserialize(rs.getString("tags")),
                rs.getInt("votes"),
                rs.getInt("answers_count"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}

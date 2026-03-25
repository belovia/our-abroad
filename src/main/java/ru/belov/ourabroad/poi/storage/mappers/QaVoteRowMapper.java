package ru.belov.ourabroad.poi.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.core.domain.Vote;
import ru.belov.ourabroad.core.enums.VoteType;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class QaVoteRowMapper implements RowMapper<Vote> {

    @Override
    public Vote mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Vote.create(
                rs.getString("id"),
                rs.getString("user_id"),
                rs.getString("entity_id"),
                VoteType.valueOf(rs.getString("vote_type"))
        );
    }
}

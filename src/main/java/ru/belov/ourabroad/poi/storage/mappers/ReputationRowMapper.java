package ru.belov.ourabroad.poi.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.core.domain.Reputation;

import java.sql.ResultSet;
import java.sql.SQLException;
@Component
public class ReputationRowMapper implements RowMapper<Reputation> {

    @Override
    public Reputation mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Reputation.create(
                rs.getString("user_id"),
                rs.getInt("score"),
                rs.getInt("level"));
    }
}

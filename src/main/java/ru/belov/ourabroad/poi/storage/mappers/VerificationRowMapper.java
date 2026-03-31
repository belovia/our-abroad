package ru.belov.ourabroad.poi.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.belov.ourabroad.core.domain.Verification;
import ru.belov.ourabroad.core.domain.VerificationFactory;
import ru.belov.ourabroad.core.enums.VerificationStatus;
import ru.belov.ourabroad.core.enums.VerificationType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class VerificationRowMapper implements RowMapper<Verification> {

    @Override
    public Verification mapRow(ResultSet rs, int rowNum) throws SQLException {

        Timestamp verifiedTs = rs.getTimestamp("verified_at");
        LocalDateTime verifiedAt = verifiedTs != null ? verifiedTs.toLocalDateTime() : null;

        return VerificationFactory.fromDb(
                rs.getString("id"),
                rs.getString("user_id"),
                VerificationType.valueOf(rs.getString("type")),
                rs.getString("related_entity_id"),
                VerificationStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("created_at").toLocalDateTime(),
                verifiedAt);
    }
}

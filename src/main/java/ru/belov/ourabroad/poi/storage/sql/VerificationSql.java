package ru.belov.ourabroad.poi.storage.sql;

public class VerificationSql {

    public static final String FIND_BY_ID = """
        SELECT id, user_id, type, related_entity_id,
               status, created_at, verified_at
        FROM verifications
        WHERE id = :id
    """;

    public static final String FIND_BY_USER_ID = """
        SELECT id, user_id, type, related_entity_id,
               status, created_at, verified_at
        FROM verifications
        WHERE user_id = :userId
    """;

    public static final String INSERT = """
        INSERT INTO verifications (
            id, user_id, type, related_entity_id,
            status, created_at, verified_at
        )
        VALUES (
            :id, :userId, :type, :relatedEntityId,
            :status, :createdAt, :verifiedAt
        )
    """;

    public static final String UPDATE_STATUS = """
        UPDATE verifications
        SET
            status = :status,
            verified_at = :verifiedAt
        WHERE id = :id
    """;
}


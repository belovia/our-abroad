package ru.belov.ourabroad.poi.storage.sql;

public class ReputationSql {
    private ReputationSql() {}

    public static final String FIND_BY_USER_ID = """
        SELECT user_id, score, level
        FROM reputations
        WHERE user_id = :userId
    """;

    public static final String INSERT = """
        INSERT INTO reputations (
            user_id,
            score,
            level
        ) VALUES (
            :userId,
            :score,
            :level
        )
    """;

    public static final String UPDATE = """
        UPDATE reputations
        SET
            score = :score,
            level = :level
        WHERE user_id = :userId
    """;
}

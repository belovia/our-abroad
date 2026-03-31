package ru.belov.ourabroad.poi.storage.sql;

public class ReputationSql {
    private ReputationSql() {}

    public static final String FIND_BY_USER_ID = """
        SELECT user_id, score, level
        FROM reputations
        WHERE user_id = :userId
    """;

    public static final String UPSERT = """
        INSERT INTO reputations (
            user_id,
            score,
            level
        ) VALUES (
            :userId,
            :score,
            :level
        )
        ON CONFLICT (user_id) DO UPDATE SET
            score = EXCLUDED.score,
            level = EXCLUDED.level
    """;
}

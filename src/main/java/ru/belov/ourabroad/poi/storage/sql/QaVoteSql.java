package ru.belov.ourabroad.poi.storage.sql;

public final class QaVoteSql {

    private QaVoteSql() {}

    public static final String FIND_BY_USER_AND_ENTITY = """
        SELECT id, user_id, entity_id, vote_type
        FROM qa_votes
        WHERE user_id = :userId AND entity_id = :entityId
        """;

    public static final String INSERT = """
        INSERT INTO qa_votes (id, user_id, entity_id, vote_type)
        VALUES (:id, :userId, :entityId, :voteType)
        """;

    public static final String UPDATE_TYPE = """
        UPDATE qa_votes SET vote_type = :voteType WHERE id = :id
        """;

    public static final String DELETE_BY_ID = """
        DELETE FROM qa_votes WHERE id = :id
        """;
}

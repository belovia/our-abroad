package ru.belov.ourabroad.poi.storage.sql;

public final class AnswerSql {

    private AnswerSql() {}

    public static final String FIND_BY_ID = """
        SELECT id, question_id, author_id, specialist_profile_id, content, votes, accepted, created_at
        FROM qa_answers
        WHERE id = :id
        """;

    public static final String FIND_BY_QUESTION_ID = """
        SELECT id, question_id, author_id, specialist_profile_id, content, votes, accepted, created_at
        FROM qa_answers
        WHERE question_id = :questionId
        ORDER BY created_at ASC
        """;

    public static final String FIND_BY_QUESTION_ID_SORTED = """
        SELECT id, question_id, author_id, specialist_profile_id, content, votes, accepted, created_at
        FROM qa_answers
        WHERE question_id = :questionId
        ORDER BY accepted DESC, votes DESC, created_at ASC
        """;

    public static final String FIND_ACCEPTED_BY_QUESTION_ID = """
        SELECT id, question_id, author_id, specialist_profile_id, content, votes, accepted, created_at
        FROM qa_answers
        WHERE question_id = :questionId AND accepted = TRUE
        LIMIT 1
        """;

    public static final String INSERT = """
        INSERT INTO qa_answers (
            id, question_id, author_id, specialist_profile_id, content, votes, accepted, created_at
        ) VALUES (
            :id, :questionId, :authorId, :specialistProfileId, :content, :votes, :accepted, :createdAt
        )
        """;

    public static final String CLEAR_ACCEPTED_BY_QUESTION_ID = """
        UPDATE qa_answers
        SET accepted = FALSE
        WHERE question_id = :questionId AND accepted = TRUE
        """;

    public static final String SET_ACCEPTED = """
        UPDATE qa_answers
        SET accepted = :accepted
        WHERE id = :id
        """;

    public static final String ADD_VOTE_DELTA = """
        UPDATE qa_answers SET votes = votes + :delta WHERE id = :id
        """;
}

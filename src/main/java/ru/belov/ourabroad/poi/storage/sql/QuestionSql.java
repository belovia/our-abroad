package ru.belov.ourabroad.poi.storage.sql;

public final class QuestionSql {

    private QuestionSql() {}

    public static final String FIND_BY_ID = """
        SELECT id, author_id, title, content, tags, votes, answers_count, created_at
        FROM qa_questions
        WHERE id = :id
        """;

    public static final String INSERT = """
        INSERT INTO qa_questions (
            id, author_id, title, content, tags, votes, answers_count, created_at
        ) VALUES (
            :id, :authorId, :title, :content, :tags, :votes, :answersCount, :createdAt
        )
        """;

    public static final String UPDATE_VOTES = """
        UPDATE qa_questions SET votes = :votes WHERE id = :id
        """;

    public static final String INCREMENT_ANSWERS_COUNT = """
        UPDATE qa_questions SET answers_count = answers_count + 1 WHERE id = :id
        """;

    public static final String ADD_VOTE_DELTA = """
        UPDATE qa_questions SET votes = votes + :delta WHERE id = :id
        """;
}

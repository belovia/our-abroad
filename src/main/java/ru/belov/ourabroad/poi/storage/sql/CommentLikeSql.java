package ru.belov.ourabroad.poi.storage.sql;

public final class CommentLikeSql {

    private CommentLikeSql() {}

    public static final String INSERT = """
            INSERT INTO comment_likes (id, user_id, comment_id)
            VALUES (:id, :userId, :commentId)
            """;

    public static final String FIND_BY_USER_AND_COMMENT = """
            SELECT id, user_id, comment_id
            FROM comment_likes
            WHERE user_id = :userId AND comment_id = :commentId
            """;

    public static final String DELETE_BY_USER_AND_COMMENT = """
            DELETE FROM comment_likes
            WHERE user_id = :userId AND comment_id = :commentId
            """;
}

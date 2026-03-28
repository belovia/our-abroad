package ru.belov.ourabroad.poi.storage.sql;

public final class CommentSql {

    private CommentSql() {}

    public static final String INSERT = """
            INSERT INTO comments (
                id, author_id, entity_id, entity_type, parent_id, content, likes, replies_count, created_at
            ) VALUES (
                :id, :authorId, :entityId, :entityType, :parentId, :content, :likes, :repliesCount, :createdAt
            )
            """;

    public static final String FIND_BY_ID = """
            SELECT id, author_id, entity_id, entity_type, parent_id, content, likes, replies_count, created_at
            FROM comments
            WHERE id = :id
            """;

    public static final String FIND_ROOTS_BY_ENTITY = """
            SELECT id, author_id, entity_id, entity_type, parent_id, content, likes, replies_count, created_at
            FROM comments
            WHERE entity_id = :entityId AND entity_type = :entityType AND parent_id IS NULL
            """;

    public static final String FIND_BY_PARENT_IDS = """
            SELECT id, author_id, entity_id, entity_type, parent_id, content, likes, replies_count, created_at
            FROM comments
            WHERE entity_id = :entityId AND entity_type = :entityType AND parent_id IN (:parentIds)
            ORDER BY parent_id ASC, created_at ASC
            """;

    public static final String INCREMENT_REPLIES = """
            UPDATE comments SET replies_count = replies_count + 1 WHERE id = :id
            """;

    public static final String UPDATE_LIKES = """
            UPDATE comments SET likes = likes + :delta WHERE id = :id
            """;
}

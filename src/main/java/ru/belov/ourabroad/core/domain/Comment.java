package ru.belov.ourabroad.core.domain;

import lombok.Getter;
import lombok.Setter;
import ru.belov.ourabroad.core.enums.CommentEntityType;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
public class Comment {

    private final String id;
    private final String authorId;
    private final String entityId;
    private final CommentEntityType entityType;
    private final String parentId;
    private String content;
    private int likes;
    private int repliesCount;
    private final LocalDateTime createdAt;

    private Comment(
            String id,
            String authorId,
            String entityId,
            CommentEntityType entityType,
            String parentId,
            String content,
            int likes,
            int repliesCount,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.authorId = authorId;
        this.entityId = entityId;
        this.entityType = entityType;
        this.parentId = parentId;
        this.content = content;
        this.likes = likes;
        this.repliesCount = repliesCount;
        this.createdAt = createdAt;
    }

    public static Comment create(
            String id,
            String authorId,
            String entityId,
            CommentEntityType entityType,
            String parentId,
            String content,
            int likes,
            int repliesCount,
            LocalDateTime createdAt
    ) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(authorId);
        Objects.requireNonNull(entityId);
        Objects.requireNonNull(entityType);
        Objects.requireNonNull(content);
        LocalDateTime ts = createdAt != null ? createdAt : LocalDateTime.now();
        return new Comment(id, authorId, entityId, entityType, parentId, content, likes, repliesCount, ts);
    }
}

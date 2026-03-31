package ru.belov.ourabroad.poi.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.belov.ourabroad.core.domain.Comment;
import ru.belov.ourabroad.core.enums.CommentEntityType;
import ru.belov.ourabroad.poi.storage.CommentRepository;
import ru.belov.ourabroad.poi.storage.mappers.CommentRowMapper;
import ru.belov.ourabroad.poi.storage.sql.CommentSql;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final CommentRowMapper rowMapper;

    @Override
    public void save(Comment comment) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", comment.getId());
        params.put("authorId", comment.getAuthorId());
        params.put("entityId", comment.getEntityId());
        params.put("entityType", comment.getEntityType().name());
        params.put("parentId", comment.getParentId());
        params.put("content", comment.getContent());
        params.put("likes", comment.getLikes());
        params.put("repliesCount", comment.getRepliesCount());
        params.put("createdAt", Timestamp.valueOf(comment.getCreatedAt()));
        jdbc.update(CommentSql.INSERT, params);
    }

    @Override
    public Optional<Comment> findById(String id) {
        return jdbc.query(CommentSql.FIND_BY_ID, Map.of("id", id), rowMapper).stream().findFirst();
    }

    @Override
    public List<Comment> findRootsByEntity(
            String entityId,
            CommentEntityType entityType,
            Sort sort,
            Pageable pageable
    ) {
        String sql = CommentSql.FIND_ROOTS_BY_ENTITY + " " + orderByClause(sort);
        Map<String, Object> params = new HashMap<>();
        params.put("entityId", entityId);
        params.put("entityType", entityType.name());
        if (pageable != null && pageable.isPaged()) {
            sql += " LIMIT :limit OFFSET :offset";
            params.put("limit", pageable.getPageSize());
            params.put("offset", pageable.getOffset());
        }
        return jdbc.query(sql, params, rowMapper);
    }

    @Override
    public List<Comment> findByParentIds(
            Collection<String> parentIds,
            String entityId,
            CommentEntityType entityType
    ) {
        if (parentIds == null || parentIds.isEmpty()) {
            return List.of();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("parentIds", parentIds);
        params.put("entityId", entityId);
        params.put("entityType", entityType.name());
        return jdbc.query(CommentSql.FIND_BY_PARENT_IDS, params, rowMapper);
    }

    @Override
    public boolean incrementRepliesCount(String commentId) {
        return jdbc.update(CommentSql.INCREMENT_REPLIES, Map.of("id", commentId)) > 0;
    }

    @Override
    public boolean updateLikes(String commentId, int delta) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", commentId);
        params.put("delta", delta);
        return jdbc.update(CommentSql.UPDATE_LIKES, params) > 0;
    }

    private static String orderByClause(Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return "ORDER BY created_at DESC";
        }
        List<String> parts = new ArrayList<>();
        for (Sort.Order o : sort) {
            String column = sortColumn(o.getProperty());
            if (column != null) {
                parts.add(column + " " + (o.isAscending() ? "ASC" : "DESC"));
            }
        }
        if (parts.isEmpty()) {
            return "ORDER BY created_at DESC";
        }
        return "ORDER BY " + String.join(", ", parts);
    }

    private static String sortColumn(String property) {
        if (property == null) {
            return null;
        }
        return switch (property) {
            case "createdAt" -> "created_at";
            case "likes" -> "likes";
            default -> null;
        };
    }
}

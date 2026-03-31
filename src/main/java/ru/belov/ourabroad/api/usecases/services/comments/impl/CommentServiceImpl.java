package ru.belov.ourabroad.api.usecases.services.comments.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.belov.ourabroad.api.usecases.services.comments.CommentService;
import ru.belov.ourabroad.api.usecases.services.comments.CommentThreadView;
import ru.belov.ourabroad.core.domain.Comment;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.enums.CommentEntityType;
import ru.belov.ourabroad.poi.storage.CommentRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static ru.belov.ourabroad.web.validators.ErrorCode.COMMENT_NESTING_TOO_DEEP;
import static ru.belov.ourabroad.web.validators.ErrorCode.COMMENT_NOT_FOUND;
import static ru.belov.ourabroad.web.validators.ErrorCode.FIELD_REQUIRED;
import static ru.belov.ourabroad.web.validators.ErrorCode.REQUEST_VALIDATION_ERROR;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Override
    public String createComment(
            String authorId,
            String entityId,
            CommentEntityType entityType,
            String content,
            String parentId,
            Context context
    ) {
        if (!context.isSuccess()) {
            return null;
        }
        log.info(
                "[entityId: {}][entityType: {}][authorId: {}][parentId: {}] Create comment",
                entityId,
                entityType,
                authorId,
                parentId
        );

        String trimmed = content != null ? content.trim() : "";
        if (!StringUtils.hasText(trimmed)) {
            context.setError(FIELD_REQUIRED);
            return null;
        }

        if (StringUtils.hasText(parentId)) {
            Optional<Comment> parentOpt = commentRepository.findById(parentId);
            if (parentOpt.isEmpty()) {
                log.warn("[parentId: {}] Parent comment not found", parentId);
                context.setError(COMMENT_NOT_FOUND);
                return null;
            }
            Comment parent = parentOpt.get();
            if (!entityId.equals(parent.getEntityId()) || entityType != parent.getEntityType()) {
                log.warn("[parentId: {}] Parent belongs to another entity", parentId);
                context.setError(REQUEST_VALIDATION_ERROR);
                return null;
            }
            if (parent.getParentId() != null) {
                log.warn("[parentId: {}] Nesting deeper than one reply level is not allowed", parentId);
                context.setError(COMMENT_NESTING_TOO_DEEP);
                return null;
            }
            if (!commentRepository.incrementRepliesCount(parentId)) {
                log.warn("[parentId: {}] incrementRepliesCount affected no rows", parentId);
                context.setError(COMMENT_NOT_FOUND);
                return null;
            }
        }

        String id = UUID.randomUUID().toString();
        Comment comment = Comment.create(
                id,
                authorId,
                entityId,
                entityType,
                StringUtils.hasText(parentId) ? parentId : null,
                trimmed,
                0,
                0,
                null
        );
        commentRepository.save(comment);
        log.info("[commentId: {}] Comment saved", id);
        return id;
    }

    @Override
    public List<CommentThreadView> getCommentThreads(
            String entityId,
            CommentEntityType entityType,
            Sort sort,
            Pageable pageable,
            Context context
    ) {
        if (!context.isSuccess()) {
            return List.of();
        }
        log.info("[entityId: {}][entityType: {}] Load comment threads", entityId, entityType);

        List<Comment> roots = commentRepository.findRootsByEntity(entityId, entityType, sort, pageable);
        if (roots.isEmpty()) {
            return List.of();
        }

        List<String> rootIds = roots.stream().map(Comment::getId).toList();
        List<Comment> replies = commentRepository.findByParentIds(rootIds, entityId, entityType);

        Map<String, List<Comment>> byParent = new LinkedHashMap<>();
        for (String rootId : rootIds) {
            byParent.put(rootId, new ArrayList<>());
        }
        for (Comment reply : replies) {
            byParent.computeIfAbsent(reply.getParentId(), k -> new ArrayList<>()).add(reply);
        }
        for (List<Comment> list : byParent.values()) {
            list.sort(Comparator.comparing(Comment::getCreatedAt));
        }

        List<CommentThreadView> views = new ArrayList<>(roots.size());
        for (Comment root : roots) {
            views.add(new CommentThreadView(root, List.copyOf(byParent.getOrDefault(root.getId(), List.of()))));
        }
        return views;
    }
}

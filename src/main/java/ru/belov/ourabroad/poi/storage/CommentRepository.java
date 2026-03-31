package ru.belov.ourabroad.poi.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.belov.ourabroad.core.domain.Comment;
import ru.belov.ourabroad.core.enums.CommentEntityType;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommentRepository {

    void save(Comment comment);

    Optional<Comment> findById(String id);

    List<Comment> findRootsByEntity(
            String entityId,
            CommentEntityType entityType,
            Sort sort,
            Pageable pageable
    );

    List<Comment> findByParentIds(
            Collection<String> parentIds,
            String entityId,
            CommentEntityType entityType
    );

    boolean incrementRepliesCount(String commentId);

    boolean updateLikes(String commentId, int delta);
}

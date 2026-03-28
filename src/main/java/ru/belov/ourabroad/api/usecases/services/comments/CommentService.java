package ru.belov.ourabroad.api.usecases.services.comments;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.enums.CommentEntityType;

import java.util.List;

public interface CommentService {

    /**
     * @return id сохранённого комментария или {@code null} при ошибке (см. {@link Context})
     */
    String createComment(
            String authorId,
            String entityId,
            CommentEntityType entityType,
            String content,
            String parentId,
            Context context
    );

    List<CommentThreadView> getCommentThreads(
            String entityId,
            CommentEntityType entityType,
            Sort sort,
            Pageable pageable,
            Context context
    );
}

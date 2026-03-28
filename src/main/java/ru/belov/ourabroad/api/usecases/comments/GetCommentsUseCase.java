package ru.belov.ourabroad.api.usecases.comments;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.belov.ourabroad.api.usecases.services.comments.CommentThreadView;

import java.util.List;

public interface GetCommentsUseCase {

    Response execute(Request request);

    record Request(
            String entityId,
            String entityType,
            Sort sort,
            Pageable pageable
    ) {
    }

    record Response(List<CommentThreadView> threads, boolean success, String errorMessage) {
    }
}

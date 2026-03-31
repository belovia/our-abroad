package ru.belov.ourabroad.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.belov.ourabroad.api.usecases.comments.CreateCommentUseCase;
import ru.belov.ourabroad.api.usecases.comments.GetCommentsUseCase;
import ru.belov.ourabroad.api.usecases.comments.LikeCommentUseCase;
import ru.belov.ourabroad.web.dto.comment.CreateCommentBody;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CreateCommentUseCase createCommentUseCase;
    private final GetCommentsUseCase getCommentsUseCase;
    private final LikeCommentUseCase likeCommentUseCase;

    @PostMapping
    public ResponseEntity<CreateCommentUseCase.Response> createComment(@RequestBody CreateCommentBody body) {
        log.info("[userId: {}] POST /api/comments", body.authorId());
        var request = new CreateCommentUseCase.Request(
                body.authorId(),
                body.entityId(),
                body.entityType(),
                body.content(),
                body.parentId()
        );
        return ResponseEntity.ok(createCommentUseCase.execute(request));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<LikeCommentUseCase.Response> likeComment(
            @PathVariable("id") String commentId,
            @RequestParam("userId") String userId
    ) {
        log.info("[commentId: {}][userId: {}] POST /api/comments/{id}/like", commentId, userId);
        return ResponseEntity.ok(likeCommentUseCase.execute(new LikeCommentUseCase.Request(userId, commentId)));
    }

    @GetMapping
    public ResponseEntity<GetCommentsUseCase.Response> getComments(
            @RequestParam String entityId,
            @RequestParam String entityType,
            @RequestParam(required = false) List<String> sort,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        log.info("[entityId: {}][entityType: {}] GET /api/comments", entityId, entityType);
        Sort sortSpec = parseSort(sort);
        Pageable pageable = toPageable(page, size);
        var request = new GetCommentsUseCase.Request(entityId, entityType, sortSpec, pageable);
        return ResponseEntity.ok(getCommentsUseCase.execute(request));
    }

    private static Sort parseSort(List<String> sortParams) {
        if (sortParams == null || sortParams.isEmpty()) {
            return Sort.by(Sort.Order.desc("createdAt"));
        }
        List<Sort.Order> orders = new ArrayList<>();
        for (String param : sortParams) {
            if (param == null || param.isBlank()) {
                continue;
            }
            String[] parts = param.split(",", 2);
            String property = parts[0].trim();
            Sort.Direction direction = Sort.Direction.DESC;
            if (parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim())) {
                direction = Sort.Direction.ASC;
            }
            orders.add(new Sort.Order(direction, property));
        }
        if (orders.isEmpty()) {
            return Sort.by(Sort.Order.desc("createdAt"));
        }
        return Sort.by(orders);
    }

    private static Pageable toPageable(Integer page, Integer size) {
        if (page == null || size == null || page < 0 || size <= 0) {
            return Pageable.unpaged();
        }
        return PageRequest.of(page, size);
    }
}

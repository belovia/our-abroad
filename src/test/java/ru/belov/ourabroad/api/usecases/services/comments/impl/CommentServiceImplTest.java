package ru.belov.ourabroad.api.usecases.services.comments.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.services.comments.CommentService;
import ru.belov.ourabroad.api.usecases.services.comments.CommentThreadView;
import ru.belov.ourabroad.core.domain.Comment;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.enums.CommentEntityType;
import ru.belov.ourabroad.poi.storage.CommentRepository;
import ru.belov.ourabroad.web.validators.ErrorCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CommentServiceImpl.class)
class CommentServiceImplTest {

    private static final String ENTITY_ID = "e-1";
    private static final CommentEntityType ENTITY_TYPE = CommentEntityType.ANSWER;
    private static final String AUTHOR = "u-1";
    private static final String PARENT_ID = "p-1";
    private static final String REPLY_PARENT_ID = "r-1";

    @MockitoBean
    private CommentRepository commentRepository;

    @Autowired
    private CommentService commentService;

    @Test
    void contextCreated() {
        assertNotNull(commentService);
    }

    @Test
    void WHEN_createRootComment_THEN_savedWithoutIncrement() {
        Context context = new Context();

        String id = commentService.createComment(AUTHOR, ENTITY_ID, ENTITY_TYPE, "  text  ", null, context);

        assertThat(context.isSuccess()).isTrue();
        assertThat(id).isNotNull();
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());
        assertThat(captor.getValue().getContent()).isEqualTo("text");
        assertThat(captor.getValue().getParentId()).isNull();
        verify(commentRepository, never()).incrementRepliesCount(any());
    }

    @Test
    void WHEN_createReply_THEN_incrementsParentRepliesCount() {
        Comment parent = Comment.create(
                PARENT_ID, AUTHOR, ENTITY_ID, ENTITY_TYPE, null, "root", 0, 0, LocalDateTime.now()
        );
        when(commentRepository.findById(PARENT_ID)).thenReturn(Optional.of(parent));
        when(commentRepository.incrementRepliesCount(PARENT_ID)).thenReturn(true);
        Context context = new Context();

        String id = commentService.createComment(AUTHOR, ENTITY_ID, ENTITY_TYPE, "reply", PARENT_ID, context);

        assertThat(context.isSuccess()).isTrue();
        assertThat(id).isNotNull();
        verify(commentRepository).incrementRepliesCount(PARENT_ID);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void WHEN_parentMissing_THEN_errorAndNoSave() {
        when(commentRepository.findById(PARENT_ID)).thenReturn(Optional.empty());
        Context context = new Context();

        String id = commentService.createComment(AUTHOR, ENTITY_ID, ENTITY_TYPE, "reply", PARENT_ID, context);

        assertThat(id).isNull();
        assertThat(context.isSuccess()).isFalse();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.COMMENT_NOT_FOUND);
        verify(commentRepository, never()).save(any());
        verify(commentRepository, never()).incrementRepliesCount(any());
    }

    @Test
    void WHEN_replyToReply_THEN_nestingError() {
        Comment nonRootParent = Comment.create(
                REPLY_PARENT_ID,
                AUTHOR,
                ENTITY_ID,
                ENTITY_TYPE,
                PARENT_ID,
                "nested parent",
                0,
                0,
                LocalDateTime.now()
        );
        when(commentRepository.findById(REPLY_PARENT_ID)).thenReturn(Optional.of(nonRootParent));
        Context context = new Context();

        String id = commentService.createComment(AUTHOR, ENTITY_ID, ENTITY_TYPE, "deep", REPLY_PARENT_ID, context);

        assertThat(id).isNull();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.COMMENT_NESTING_TOO_DEEP);
        verify(commentRepository, never()).save(any());
        verify(commentRepository, never()).incrementRepliesCount(any());
    }

    @Test
    void WHEN_getThreads_THEN_batchLoadsRepliesOnce() {
        Comment root1 = Comment.create("r1", AUTHOR, ENTITY_ID, ENTITY_TYPE, null, "a", 0, 0, LocalDateTime.now());
        Comment root2 = Comment.create("r2", AUTHOR, ENTITY_ID, ENTITY_TYPE, null, "b", 0, 0, LocalDateTime.now());
        when(commentRepository.findRootsByEntity(eq(ENTITY_ID), eq(ENTITY_TYPE), any(Sort.class), any(Pageable.class)))
                .thenReturn(List.of(root1, root2));
        when(commentRepository.findByParentIds(List.of("r1", "r2"), ENTITY_ID, ENTITY_TYPE))
                .thenReturn(List.of(
                        Comment.create("c1", AUTHOR, ENTITY_ID, ENTITY_TYPE, "r1", "rep", 0, 0, LocalDateTime.now())
                ));
        Context context = new Context();

        List<CommentThreadView> threads = commentService.getCommentThreads(
                ENTITY_ID,
                ENTITY_TYPE,
                Sort.by(Sort.Order.desc("createdAt")),
                Pageable.unpaged(),
                context
        );

        assertThat(context.isSuccess()).isTrue();
        assertThat(threads).hasSize(2);
        assertThat(threads.getFirst().replies()).hasSize(1);
        verify(commentRepository, times(1)).findByParentIds(List.of("r1", "r2"), ENTITY_ID, ENTITY_TYPE);
    }
}

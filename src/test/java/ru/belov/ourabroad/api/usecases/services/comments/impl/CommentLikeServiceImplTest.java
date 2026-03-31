package ru.belov.ourabroad.api.usecases.services.comments.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.services.comments.CommentLikeService;
import ru.belov.ourabroad.api.usecases.services.comments.CommentLikeToggleResult;
import ru.belov.ourabroad.core.domain.Comment;
import ru.belov.ourabroad.core.domain.CommentLike;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.enums.CommentEntityType;
import ru.belov.ourabroad.poi.storage.CommentLikeRepository;
import ru.belov.ourabroad.poi.storage.CommentRepository;
import ru.belov.ourabroad.web.validators.ErrorCode;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CommentLikeServiceImpl.class)
class CommentLikeServiceImplTest {

    private static final String USER = "u-1";
    private static final String COMMENT_ID = "c-1";

    @MockitoBean
    private CommentRepository commentRepository;

    @MockitoBean
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private CommentLikeService commentLikeService;

    @Test
    void contextCreated() {
        assertNotNull(commentLikeService);
    }

    @Test
    void WHEN_firstLike_THEN_saveAndIncrementLikes() {
        Comment comment = Comment.create(
                COMMENT_ID, USER, "e1", CommentEntityType.ANSWER, null, "x", 0, 0, LocalDateTime.now()
        );
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(commentLikeRepository.findByUserIdAndCommentId(USER, COMMENT_ID)).thenReturn(Optional.empty());
        when(commentRepository.updateLikes(COMMENT_ID, 1)).thenReturn(true);
        Context context = new Context();

        CommentLikeToggleResult result = commentLikeService.likeComment(USER, COMMENT_ID, context);

        assertThat(context.isSuccess()).isTrue();
        assertThat(result.likedAfter()).isTrue();
        verify(commentLikeRepository).save(any(CommentLike.class));
        verify(commentRepository).updateLikes(COMMENT_ID, 1);
        verify(commentLikeRepository, never()).deleteByUserIdAndCommentId(any(), any());
    }

    @Test
    void WHEN_secondLike_THEN_deleteAndDecrementLikes() {
        Comment comment = Comment.create(
                COMMENT_ID, USER, "e1", CommentEntityType.ANSWER, null, "x", 1, 0, LocalDateTime.now()
        );
        CommentLike existing = CommentLike.create(USER, COMMENT_ID);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(commentLikeRepository.findByUserIdAndCommentId(USER, COMMENT_ID)).thenReturn(Optional.of(existing));
        when(commentRepository.updateLikes(COMMENT_ID, -1)).thenReturn(true);
        Context context = new Context();

        CommentLikeToggleResult result = commentLikeService.likeComment(USER, COMMENT_ID, context);

        assertThat(context.isSuccess()).isTrue();
        assertThat(result.likedAfter()).isFalse();
        verify(commentLikeRepository).deleteByUserIdAndCommentId(USER, COMMENT_ID);
        verify(commentRepository).updateLikes(COMMENT_ID, -1);
        verify(commentLikeRepository, never()).save(any());
    }

    @Test
    void WHEN_commentMissing_THEN_errorAndNoLikeChanges() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());
        Context context = new Context();

        CommentLikeToggleResult result = commentLikeService.likeComment(USER, COMMENT_ID, context);

        assertThat(result).isNull();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.COMMENT_NOT_FOUND);
        verify(commentLikeRepository, never()).save(any());
        verify(commentLikeRepository, never()).deleteByUserIdAndCommentId(any(), any());
        verify(commentRepository, never()).updateLikes(any(), anyInt());
    }
}

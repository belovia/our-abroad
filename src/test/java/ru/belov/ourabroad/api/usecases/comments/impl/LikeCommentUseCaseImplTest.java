package ru.belov.ourabroad.api.usecases.comments.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.comments.LikeCommentUseCase;
import ru.belov.ourabroad.api.usecases.services.comments.CommentLikeService;
import ru.belov.ourabroad.api.usecases.services.comments.CommentLikeToggleResult;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.FieldValidator;
import ru.belov.ourabroad.web.validators.UserValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                LikeCommentUseCaseImpl.class,
                UserValidator.class,
                FieldValidator.class
        }
)
class LikeCommentUseCaseImplTest {

    private static final String USER = "u-1";
    private static final String COMMENT_ID = "c-1";

    @MockitoBean
    private CommentLikeService commentLikeService;

    @Autowired
    private LikeCommentUseCase useCase;

    @Test
    void contextCreated() {
        assertNotNull(useCase);
    }

    @Test
    void WHEN_userIdMissing_THEN_errorAndNoServiceCall() {
        LikeCommentUseCase.Response response = useCase.execute(new LikeCommentUseCase.Request("", COMMENT_ID));

        assertThat(response.success()).isFalse();
        assertThat(response.liked()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.USER_ID_REQUIRED.getMessage());
        verify(commentLikeService, never()).likeComment(any(), any(), any());
    }

    @Test
    void WHEN_likeSucceeds_THEN_returnsLikedFlag() {
        when(commentLikeService.likeComment(eq(USER), eq(COMMENT_ID), any(Context.class)))
                .thenReturn(new CommentLikeToggleResult(true));

        LikeCommentUseCase.Response response = useCase.execute(new LikeCommentUseCase.Request(USER, COMMENT_ID));

        assertThat(response.success()).isTrue();
        assertThat(response.liked()).isTrue();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
        verify(commentLikeService).likeComment(eq(USER), eq(COMMENT_ID), any(Context.class));
    }

    @Test
    void WHEN_serviceSetsError_THEN_noSuccess() {
        when(commentLikeService.likeComment(eq(USER), eq(COMMENT_ID), any(Context.class)))
                .thenAnswer(invocation -> {
                    Context ctx = invocation.getArgument(2);
                    ctx.setError(ErrorCode.COMMENT_NOT_FOUND);
                    return null;
                });

        LikeCommentUseCase.Response response = useCase.execute(new LikeCommentUseCase.Request(USER, COMMENT_ID));

        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.COMMENT_NOT_FOUND.getMessage());
    }
}

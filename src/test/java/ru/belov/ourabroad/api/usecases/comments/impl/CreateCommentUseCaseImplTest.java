package ru.belov.ourabroad.api.usecases.comments.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.comments.CreateCommentUseCase;
import ru.belov.ourabroad.api.usecases.services.comments.CommentService;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.UserFactory;
import ru.belov.ourabroad.core.enums.CommentEntityType;
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
                CreateCommentUseCaseImpl.class,
                UserValidator.class,
                FieldValidator.class
        }
)
class CreateCommentUseCaseImplTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CommentService commentService;

    @Autowired
    private CreateCommentUseCase useCase;

    @Test
    void contextCreated() {
        assertNotNull(useCase);
    }

    @Test
    void WHEN_invalidEntityType_THEN_errorAndNoCommentService() {
        var request = new CreateCommentUseCase.Request(
                "u1",
                "e1",
                "NOT_A_TYPE",
                "hello",
                null
        );

        CreateCommentUseCase.Response response = useCase.execute(request);

        assertThat(response.success()).isFalse();
        assertThat(response.commentId()).isNull();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.REQUEST_VALIDATION_ERROR.getMessage());
        verify(commentService, never()).createComment(any(), any(), any(), any(), any(), any());
        verify(userService, never()).findById(any(), any());
    }

    @Test
    void WHEN_validRequest_THEN_createsComment() {
        when(userService.findById(eq("u1"), any(Context.class)))
                .thenReturn(UserFactory.newUser("u1", "a@b.c", "+79001112233", "Secret1", null, null, null));
        when(commentService.createComment(
                eq("u1"),
                eq("e1"),
                eq(CommentEntityType.ANSWER),
                eq("hello"),
                eq(null),
                any(Context.class)
        )).thenReturn("new-id");

        var request = new CreateCommentUseCase.Request(
                "u1",
                "e1",
                "ANSWER",
                "hello",
                null
        );

        CreateCommentUseCase.Response response = useCase.execute(request);

        assertThat(response.success()).isTrue();
        assertThat(response.commentId()).isEqualTo("new-id");
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
        verify(commentService).createComment(
                eq("u1"),
                eq("e1"),
                eq(CommentEntityType.ANSWER),
                eq("hello"),
                eq(null),
                any(Context.class)
        );
    }
}

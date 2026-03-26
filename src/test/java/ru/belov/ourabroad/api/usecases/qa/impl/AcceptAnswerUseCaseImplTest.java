package ru.belov.ourabroad.api.usecases.qa.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.qa.QaReputationRules;
import ru.belov.ourabroad.api.usecases.services.qa.AcceptAnswerResult;
import ru.belov.ourabroad.api.usecases.services.qa.AnswerService;
import ru.belov.ourabroad.api.usecases.services.reputation.ReputationService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.web.validators.ErrorCode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AcceptAnswerUseCaseImpl.class)
class AcceptAnswerUseCaseImplTest {

    private static final String ANSWER_ID = "a-1";
    private static final String USER_ID = "user-1";
    private static final String ANSWER_AUTHOR = "answer-author";
    private static final String QUESTION_AUTHOR = "question-author";

    @MockitoBean
    private AnswerService answerService;

    @MockitoBean
    private ReputationService reputationService;

    @Autowired
    private AcceptAnswerUseCaseImpl useCase;

    @Test
    void contextCreated() {
        assertNotNull(useCase);
    }

    @Test
    void WHEN_successAccept_THEN_reputationAddedAndSuccess() {
        when(answerService.acceptAnswer(eq(ANSWER_ID), eq(USER_ID), any(Context.class)))
                .thenReturn(new AcceptAnswerResult(ANSWER_AUTHOR, QUESTION_AUTHOR, true));
        doNothing().when(reputationService).addPoints(anyString(), anyInt(), any(Context.class));

        Context context = new Context();
        useCase.execute(ANSWER_ID, USER_ID, context);

        assertThat(context.isSuccess()).isTrue();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.SUCCESS);
        verify(reputationService).addPoints(
                eq(ANSWER_AUTHOR),
                eq(QaReputationRules.POINTS_ACCEPTED_ANSWER_AUTHOR),
                any(Context.class)
        );
        verify(reputationService).addPoints(
                eq(QUESTION_AUTHOR),
                eq(QaReputationRules.POINTS_ACCEPTED_ANSWER_QUESTION_AUTHOR),
                any(Context.class)
        );
    }

    @Test
    void WHEN_answerNotFound_THEN_contextErrorAndNoReputation() {
        when(answerService.acceptAnswer(eq(ANSWER_ID), eq(USER_ID), any(Context.class)))
                .thenAnswer(invocation -> {
                    Context ctx = invocation.getArgument(2);
                    ctx.setError(ErrorCode.ANSWER_NOT_FOUND);
                    return null;
                });

        Context context = new Context();
        useCase.execute(ANSWER_ID, USER_ID, context);

        assertThat(context.isSuccess()).isFalse();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.ANSWER_NOT_FOUND);
        verify(reputationService, never()).addPoints(anyString(), anyInt(), any());
    }

    @Test
    void WHEN_questionNotFound_THEN_contextErrorAndNoReputation() {
        when(answerService.acceptAnswer(eq(ANSWER_ID), eq(USER_ID), any(Context.class)))
                .thenAnswer(invocation -> {
                    Context ctx = invocation.getArgument(2);
                    ctx.setError(ErrorCode.QUESTION_NOT_FOUND);
                    return null;
                });

        Context context = new Context();
        useCase.execute(ANSWER_ID, USER_ID, context);

        assertThat(context.isSuccess()).isFalse();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.QUESTION_NOT_FOUND);
        verify(reputationService, never()).addPoints(anyString(), anyInt(), any());
    }

    @Test
    void WHEN_permissionDenied_THEN_contextErrorAndNoReputation() {
        when(answerService.acceptAnswer(eq(ANSWER_ID), eq(USER_ID), any(Context.class)))
                .thenAnswer(invocation -> {
                    Context ctx = invocation.getArgument(2);
                    ctx.setError(ErrorCode.PERMISSION_DENIED);
                    return null;
                });

        Context context = new Context();
        useCase.execute(ANSWER_ID, USER_ID, context);

        assertThat(context.isSuccess()).isFalse();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.PERMISSION_DENIED);
        verify(reputationService, never()).addPoints(anyString(), anyInt(), any());
    }

    @Test
    void WHEN_sameAnswerAlreadyAccepted_THEN_noOpAndNoReputation() {
        when(answerService.acceptAnswer(eq(ANSWER_ID), eq(USER_ID), any(Context.class)))
                .thenReturn(new AcceptAnswerResult(ANSWER_AUTHOR, QUESTION_AUTHOR, false));

        Context context = new Context();
        useCase.execute(ANSWER_ID, USER_ID, context);

        assertThat(context.isSuccess()).isTrue();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.SUCCESS);
        verify(reputationService, never()).addPoints(anyString(), anyInt(), any());
    }
}


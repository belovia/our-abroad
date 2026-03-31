package ru.belov.ourabroad.api.usecases.services.qa.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.services.qa.AnswerService;
import ru.belov.ourabroad.api.usecases.services.qa.QuestionService;
import ru.belov.ourabroad.api.usecases.services.qa.VoteApplyResult;
import ru.belov.ourabroad.api.usecases.services.qa.VotePersistenceService;
import ru.belov.ourabroad.api.usecases.services.qa.VoteService;
import ru.belov.ourabroad.core.domain.Answer;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Question;
import ru.belov.ourabroad.core.domain.Vote;
import ru.belov.ourabroad.core.enums.VoteType;
import ru.belov.ourabroad.web.validators.ErrorCode;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = VoteServiceImpl.class)
class VoteServiceImplTest {

    private static final String QUESTION_ID = "q-1";
    private static final String ANSWER_ID = "a-1";
    private static final String VOTER = "voter-1";
    private static final String AUTHOR = "author-1";

    @MockitoBean
    private QuestionService questionService;

    @MockitoBean
    private AnswerService answerService;

    @MockitoBean
    private VotePersistenceService votePersistence;

    @Autowired
    private VoteService voteService;

    @Test
    void contextCreated() {
        assertNotNull(voteService);
    }

    @Test
    void WHEN_firstUpvoteOnQuestion_THEN_persistVoteAndApplyDelta() {
        Question q = Question.create(QUESTION_ID, AUTHOR, "t", "c", Set.of(), 0, 0, null);
        when(questionService.findByIdOrError(eq(QUESTION_ID), any(Context.class))).thenReturn(q);
        when(votePersistence.findVote(eq(VOTER), eq(QUESTION_ID), any(Context.class))).thenReturn(null);

        Context context = new Context();
        VoteApplyResult result = voteService.voteQuestion(VOTER, QUESTION_ID, VoteType.UP, context);

        assertThat(result.contentAuthorId()).isEqualTo(AUTHOR);
        assertThat(result.authorReputationDelta()).isEqualTo(1);
        assertThat(context.isSuccess()).isTrue();
        verify(votePersistence).createVote(any(Vote.class), any(Context.class));
        verify(votePersistence, never()).updateVote(any(), any());
        verify(votePersistence).applyVoteDeltaToQuestion(eq(QUESTION_ID), eq(1), any(Context.class));
        verify(votePersistence, never()).applyVoteDeltaToAnswer(anyString(), anyInt(), any());
    }

    @Test
    void WHEN_repeatSameVote_THEN_noPersistenceAndNoCounterUpdate() {
        Question q = Question.create(QUESTION_ID, AUTHOR, "t", "c", Set.of(), 3, 0, null);
        Vote existing = Vote.create("v-id", VOTER, QUESTION_ID, VoteType.UP);
        when(questionService.findByIdOrError(eq(QUESTION_ID), any(Context.class))).thenReturn(q);
        when(votePersistence.findVote(eq(VOTER), eq(QUESTION_ID), any(Context.class))).thenReturn(existing);

        Context context = new Context();
        VoteApplyResult result = voteService.voteQuestion(VOTER, QUESTION_ID, VoteType.UP, context);

        assertThat(result.authorReputationDelta()).isEqualTo(0);
        assertThat(context.isSuccess()).isTrue();
        verify(votePersistence, never()).createVote(any(), any());
        verify(votePersistence, never()).updateVote(any(), any());
        verify(votePersistence, never()).applyVoteDeltaToQuestion(anyString(), anyInt(), any());
        verify(votePersistence, never()).applyVoteDeltaToAnswer(anyString(), anyInt(), any());
    }

    @Test
    void WHEN_changeUpToDown_THEN_updateVoteAndNegativeDelta() {
        Question q = Question.create(QUESTION_ID, AUTHOR, "t", "c", Set.of(), 1, 0, null);
        Vote existing = Vote.create("v-id", VOTER, QUESTION_ID, VoteType.UP);
        when(questionService.findByIdOrError(eq(QUESTION_ID), any(Context.class))).thenReturn(q);
        when(votePersistence.findVote(eq(VOTER), eq(QUESTION_ID), any(Context.class))).thenReturn(existing);

        Context context = new Context();
        VoteApplyResult result = voteService.voteQuestion(VOTER, QUESTION_ID, VoteType.DOWN, context);

        assertThat(result.authorReputationDelta()).isEqualTo(-1);
        assertThat(context.isSuccess()).isTrue();
        verify(votePersistence).updateVote(eq(existing), any(Context.class));
        verify(votePersistence, never()).createVote(any(), any());
        verify(votePersistence).applyVoteDeltaToQuestion(eq(QUESTION_ID), eq(-2), any(Context.class));
    }

    @Test
    void WHEN_voteAnswer_THEN_usesAnswerAuthorAndAnswerDelta() {
        Answer a = Answer.create(ANSWER_ID, QUESTION_ID, AUTHOR, null, "ans", 0, false, null);
        when(answerService.findByIdOrError(eq(ANSWER_ID), any(Context.class))).thenReturn(a);
        when(votePersistence.findVote(eq(VOTER), eq(ANSWER_ID), any(Context.class))).thenReturn(null);

        Context context = new Context();
        VoteApplyResult result = voteService.voteAnswer(VOTER, ANSWER_ID, VoteType.DOWN, context);

        assertThat(result.contentAuthorId()).isEqualTo(AUTHOR);
        assertThat(result.authorReputationDelta()).isEqualTo(0);
        ArgumentCaptor<Vote> captor = ArgumentCaptor.forClass(Vote.class);
        verify(votePersistence).createVote(captor.capture(), any(Context.class));
        assertThat(captor.getValue().getType()).isEqualTo(VoteType.DOWN);
        verify(votePersistence).applyVoteDeltaToAnswer(eq(ANSWER_ID), eq(-1), any(Context.class));
        verify(votePersistence, never()).applyVoteDeltaToQuestion(anyString(), anyInt(), any());
    }

    @Test
    void WHEN_questionNotFound_THEN_stopsBeforeVotePersistence() {
        when(questionService.findByIdOrError(eq(QUESTION_ID), any(Context.class))).thenAnswer(invocation -> {
            Context ctx = invocation.getArgument(1);
            ctx.setError(ErrorCode.QUESTION_NOT_FOUND);
            return null;
        });

        Context context = new Context();
        VoteApplyResult result = voteService.voteQuestion(VOTER, QUESTION_ID, VoteType.UP, context);

        assertThat(result).isNull();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.QUESTION_NOT_FOUND);
        verify(votePersistence, never()).findVote(anyString(), anyString(), any());
        verify(votePersistence, never()).createVote(any(), any());
        verify(votePersistence, never()).applyVoteDeltaToQuestion(anyString(), anyInt(), any());
        verify(votePersistence, never()).applyVoteDeltaToAnswer(anyString(), anyInt(), any());
    }

    @Test
    void WHEN_createVoteFails_THEN_noCounterUpdate() {
        Question q = Question.create(QUESTION_ID, AUTHOR, "t", "c", Set.of(), 0, 0, null);
        when(questionService.findByIdOrError(eq(QUESTION_ID), any(Context.class))).thenReturn(q);
        when(votePersistence.findVote(eq(VOTER), eq(QUESTION_ID), any(Context.class))).thenReturn(null);
        doAnswer((org.mockito.stubbing.Answer<Void>) invocation -> {
            Context ctx = invocation.getArgument(1);
            ctx.setError(ErrorCode.VOTE_UPDATE_FAILED);
            return null;
        }).when(votePersistence).createVote(any(Vote.class), any(Context.class));

        Context context = new Context();
        VoteApplyResult result = voteService.voteQuestion(VOTER, QUESTION_ID, VoteType.UP, context);

        assertThat(result).isNull();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.VOTE_UPDATE_FAILED);
        verify(votePersistence, never()).applyVoteDeltaToQuestion(anyString(), anyInt(), any());
        verify(votePersistence, never()).applyVoteDeltaToAnswer(anyString(), anyInt(), any());
    }

    @Test
    void WHEN_applyVoteDeltaFails_THEN_contextError() {
        Question q = Question.create(QUESTION_ID, AUTHOR, "t", "c", Set.of(), 0, 0, null);
        when(questionService.findByIdOrError(eq(QUESTION_ID), any(Context.class))).thenReturn(q);
        when(votePersistence.findVote(eq(VOTER), eq(QUESTION_ID), any(Context.class))).thenReturn(null);
        doAnswer((org.mockito.stubbing.Answer<Void>) invocation -> {
            Context ctx = invocation.getArgument(2);
            ctx.setError(ErrorCode.ENTITY_VOTE_UPDATE_FAILED);
            return null;
        }).when(votePersistence).applyVoteDeltaToQuestion(eq(QUESTION_ID), eq(1), any(Context.class));

        Context context = new Context();
        VoteApplyResult result = voteService.voteQuestion(VOTER, QUESTION_ID, VoteType.UP, context);

        assertThat(result).isNull();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.ENTITY_VOTE_UPDATE_FAILED);
        verify(votePersistence).createVote(any(Vote.class), any(Context.class));
    }
}

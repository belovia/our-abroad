package ru.belov.ourabroad.api.usecases.services.qa.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.services.qa.VoteApplyResult;
import ru.belov.ourabroad.api.usecases.services.qa.VoteService;
import ru.belov.ourabroad.core.domain.Answer;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Question;
import ru.belov.ourabroad.core.domain.Vote;
import ru.belov.ourabroad.core.enums.VoteType;
import ru.belov.ourabroad.poi.storage.AnswerRepository;
import ru.belov.ourabroad.poi.storage.QuestionRepository;
import ru.belov.ourabroad.poi.storage.VoteRepository;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = VoteServiceImpl.class)
class VoteServiceImplTest {

    private static final String QUESTION_ID = "q-1";
    private static final String ANSWER_ID = "a-1";
    private static final String VOTER = "voter-1";
    private static final String AUTHOR = "author-1";

    @MockitoBean
    private QuestionRepository questionRepository;

    @MockitoBean
    private AnswerRepository answerRepository;

    @MockitoBean
    private VoteRepository voteRepository;

    @Autowired
    private VoteService voteService;

    @Test
    void contextCreated() {
        assertNotNull(voteService);
    }

    @Test
    void WHEN_firstUpvoteOnQuestion_THEN_saveVoteAndPositiveDelta() {
        Question q = Question.create(QUESTION_ID, AUTHOR, "t", "c", Set.of(), 0, 0, null);
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(q));
        when(voteRepository.findByUserIdAndEntityId(VOTER, QUESTION_ID)).thenReturn(Optional.empty());
        when(questionRepository.addVoteDelta(QUESTION_ID, 1)).thenReturn(true);

        Context context = new Context();
        VoteApplyResult result = voteService.voteQuestion(VOTER, QUESTION_ID, VoteType.UP, context);

        assertThat(result.contentAuthorId()).isEqualTo(AUTHOR);
        assertThat(result.authorReputationDelta()).isEqualTo(1);
        assertThat(context.isSuccess()).isTrue();
        verify(voteRepository).save(any(Vote.class));
        verify(questionRepository).addVoteDelta(QUESTION_ID, 1);
    }

    @Test
    void WHEN_repeatSameVote_THEN_noOp() {
        Question q = Question.create(QUESTION_ID, AUTHOR, "t", "c", Set.of(), 3, 0, null);
        Vote existing = Vote.create("v-id", VOTER, QUESTION_ID, VoteType.UP);
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(q));
        when(voteRepository.findByUserIdAndEntityId(VOTER, QUESTION_ID)).thenReturn(Optional.of(existing));

        Context context = new Context();
        VoteApplyResult result = voteService.voteQuestion(VOTER, QUESTION_ID, VoteType.UP, context);

        assertThat(result.authorReputationDelta()).isEqualTo(0);
        verify(voteRepository, never()).save(any());
        verify(voteRepository, never()).updateType(any());
        verify(questionRepository, never()).addVoteDelta(anyString(), anyInt());
    }

    @Test
    void WHEN_changeUpToDown_THEN_updateTypeAndVoteDeltaMinusTwoAndRepMinusOne() {
        Question q = Question.create(QUESTION_ID, AUTHOR, "t", "c", Set.of(), 1, 0, null);
        Vote existing = Vote.create("v-id", VOTER, QUESTION_ID, VoteType.UP);
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(q));
        when(voteRepository.findByUserIdAndEntityId(VOTER, QUESTION_ID)).thenReturn(Optional.of(existing));
        when(questionRepository.addVoteDelta(QUESTION_ID, -2)).thenReturn(true);

        Context context = new Context();
        VoteApplyResult result = voteService.voteQuestion(VOTER, QUESTION_ID, VoteType.DOWN, context);

        assertThat(result.authorReputationDelta()).isEqualTo(-1);
        verify(voteRepository).updateType(existing);
        verify(questionRepository).addVoteDelta(QUESTION_ID, -2);
    }

    @Test
    void WHEN_voteAnswer_THEN_usesAnswerAuthor() {
        Answer a = Answer.create(ANSWER_ID, QUESTION_ID, AUTHOR, "ans", 0, false, null);
        when(answerRepository.findById(ANSWER_ID)).thenReturn(Optional.of(a));
        when(voteRepository.findByUserIdAndEntityId(VOTER, ANSWER_ID)).thenReturn(Optional.empty());
        when(answerRepository.addVoteDelta(ANSWER_ID, -1)).thenReturn(true);

        Context context = new Context();
        VoteApplyResult result = voteService.voteAnswer(VOTER, ANSWER_ID, VoteType.DOWN, context);

        assertThat(result.contentAuthorId()).isEqualTo(AUTHOR);
        assertThat(result.authorReputationDelta()).isEqualTo(0);
        ArgumentCaptor<Vote> captor = ArgumentCaptor.forClass(Vote.class);
        verify(voteRepository).save(captor.capture());
        assertThat(captor.getValue().getType()).isEqualTo(VoteType.DOWN);
        verify(answerRepository).addVoteDelta(ANSWER_ID, -1);
    }
}

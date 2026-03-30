package ru.belov.ourabroad.api.usecases.services.qa.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.services.qa.AcceptAnswerResult;
import ru.belov.ourabroad.api.usecases.services.qa.AnswerService;
import ru.belov.ourabroad.api.usecases.services.specialistprofile.SpecialistProfileService;
import ru.belov.ourabroad.core.domain.Answer;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Question;
import ru.belov.ourabroad.poi.storage.AnswerRepository;
import ru.belov.ourabroad.poi.storage.QuestionRepository;
import ru.belov.ourabroad.web.validators.ErrorCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AnswerServiceImpl.class)
class AnswerServiceImplTest {

    private static final String QUESTION_ID = "q-1";
    private static final String ANSWER_ID = "a-1";
    private static final String OTHER_ANSWER_ID = "a-2";
    private static final String QUESTION_AUTHOR = "question-author";
    private static final String ANSWER_AUTHOR = "answer-author";
    private static final String OTHER_USER = "other-user";

    @MockitoBean
    private AnswerRepository answerRepository;

    @MockitoBean
    private QuestionRepository questionRepository;

    @MockitoBean
    private SpecialistProfileService specialistProfileService;

    @Autowired
    private AnswerService answerService;

    @Test
    void contextCreated() {
        assertNotNull(answerService);
    }

    @Test
    void WHEN_acceptAnswer_successNoExistingAccepted_THEN_setsAcceptedTrue() {
        Answer answer = Answer.create(ANSWER_ID, QUESTION_ID, ANSWER_AUTHOR, null, "c", 0, false, null);
        Question question = Question.create(QUESTION_ID, QUESTION_AUTHOR, "t", "c", Set.of(), 0, 0, null);
        when(answerRepository.findById(ANSWER_ID)).thenReturn(Optional.of(answer));
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(question));
        when(answerRepository.findAcceptedByQuestionId(QUESTION_ID)).thenReturn(Optional.empty());

        Context context = new Context();
        AcceptAnswerResult result = answerService.acceptAnswer(ANSWER_ID, QUESTION_AUTHOR, context);

        assertThat(context.isSuccess()).isTrue();
        assertThat(result).isNotNull();
        assertThat(result.changed()).isTrue();
        assertThat(result.answerAuthorId()).isEqualTo(ANSWER_AUTHOR);
        assertThat(result.questionAuthorId()).isEqualTo(QUESTION_AUTHOR);
        verify(answerRepository, never()).clearAcceptedByQuestionId(anyString());
        verify(answerRepository).setAccepted(ANSWER_ID, true);
    }

    @Test
    void WHEN_acceptAnswer_replaceAccepted_THEN_clearsOldAndSetsNew() {
        Answer newAnswer = Answer.create(ANSWER_ID, QUESTION_ID, ANSWER_AUTHOR, null, "c", 0, false, null);
        Answer oldAccepted = Answer.create(OTHER_ANSWER_ID, QUESTION_ID, "old-author", null, "old", 0, true, null);
        Question question = Question.create(QUESTION_ID, QUESTION_AUTHOR, "t", "c", Set.of(), 0, 0, null);
        when(answerRepository.findById(ANSWER_ID)).thenReturn(Optional.of(newAnswer));
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(question));
        when(answerRepository.findAcceptedByQuestionId(QUESTION_ID)).thenReturn(Optional.of(oldAccepted));

        Context context = new Context();
        AcceptAnswerResult result = answerService.acceptAnswer(ANSWER_ID, QUESTION_AUTHOR, context);

        assertThat(context.isSuccess()).isTrue();
        assertThat(result).isNotNull();
        assertThat(result.changed()).isTrue();
        verify(answerRepository).clearAcceptedByQuestionId(QUESTION_ID);
        verify(answerRepository).setAccepted(ANSWER_ID, true);
    }

    @Test
    void WHEN_acceptAnswer_permissionDenied_THEN_contextErrorAndNoUpdates() {
        Answer answer = Answer.create(ANSWER_ID, QUESTION_ID, ANSWER_AUTHOR, null, "c", 0, false, null);
        Question question = Question.create(QUESTION_ID, QUESTION_AUTHOR, "t", "c", Set.of(), 0, 0, null);
        when(answerRepository.findById(ANSWER_ID)).thenReturn(Optional.of(answer));
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(question));

        Context context = new Context();
        AcceptAnswerResult result = answerService.acceptAnswer(ANSWER_ID, OTHER_USER, context);

        assertThat(result).isNull();
        assertThat(context.isSuccess()).isFalse();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.PERMISSION_DENIED);
        verify(answerRepository, never()).findAcceptedByQuestionId(anyString());
        verify(answerRepository, never()).clearAcceptedByQuestionId(anyString());
        verify(answerRepository, never()).setAccepted(anyString(), anyBoolean());
    }

    @Test
    void WHEN_acceptAnswer_answerNotFound_THEN_contextErrorAndNoUpdates() {
        when(answerRepository.findById(ANSWER_ID)).thenReturn(Optional.empty());

        Context context = new Context();
        AcceptAnswerResult result = answerService.acceptAnswer(ANSWER_ID, QUESTION_AUTHOR, context);

        assertThat(result).isNull();
        assertThat(context.isSuccess()).isFalse();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.ANSWER_NOT_FOUND);
        verify(questionRepository, never()).findById(anyString());
        verify(answerRepository, never()).findAcceptedByQuestionId(anyString());
        verify(answerRepository, never()).clearAcceptedByQuestionId(anyString());
        verify(answerRepository, never()).setAccepted(anyString(), anyBoolean());
    }

    @Test
    void WHEN_getAnswersSorted_THEN_ordersAcceptedVotesAndCreatedAt() {
        LocalDateTime t1 = LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime t2 = LocalDateTime.of(2020, 1, 2, 0, 0);
        Answer a1 = Answer.create("a1", QUESTION_ID, "u1", null, "1", 5, false, t2);
        Answer a2 = Answer.create("a2", QUESTION_ID, "u2", null, "2", 1, true, t2);
        Answer a3 = Answer.create("a3", QUESTION_ID, "u3", null, "3", 10, false, t1);
        Answer a4 = Answer.create("a4", QUESTION_ID, "u4", null, "4", 1, true, t1);

        when(answerRepository.findByQuestionIdSorted(eq(QUESTION_ID), any())).thenReturn(List.of(a1, a2, a3, a4));

        Context context = new Context();
        List<Answer> sorted = answerService.getAnswersSorted(QUESTION_ID, context);

        assertThat(context.isSuccess()).isTrue();
        assertThat(sorted).extracting(Answer::getId).containsExactly("a4", "a2", "a3", "a1");
        verify(answerRepository).findByQuestionIdSorted(eq(QUESTION_ID), any());
    }
}


package ru.belov.ourabroad.api.usecases.services.qa.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.services.qa.AcceptAnswerResult;
import ru.belov.ourabroad.api.usecases.services.qa.AnswerService;
import ru.belov.ourabroad.core.domain.Answer;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Question;
import ru.belov.ourabroad.poi.storage.AnswerRepository;
import ru.belov.ourabroad.poi.storage.QuestionRepository;
import ru.belov.ourabroad.web.validators.ErrorCode;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    @Autowired
    private AnswerService answerService;

    @Test
    void contextCreated() {
        assertNotNull(answerService);
    }

    @Test
    void WHEN_acceptAnswer_successNoExistingAccepted_THEN_setsAcceptedTrue() {
        Answer answer = Answer.create(ANSWER_ID, QUESTION_ID, ANSWER_AUTHOR, "c", 0, false, null);
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
        Answer newAnswer = Answer.create(ANSWER_ID, QUESTION_ID, ANSWER_AUTHOR, "c", 0, false, null);
        Answer oldAccepted = Answer.create(OTHER_ANSWER_ID, QUESTION_ID, "old-author", "old", 0, true, null);
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
        Answer answer = Answer.create(ANSWER_ID, QUESTION_ID, ANSWER_AUTHOR, "c", 0, false, null);
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
}


package ru.belov.ourabroad.api.usecases.qa.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.qa.AnswerQuestionUseCase;
import ru.belov.ourabroad.api.usecases.qa.QaReputationRules;
import ru.belov.ourabroad.api.usecases.services.qa.AnswerService;
import ru.belov.ourabroad.api.usecases.services.qa.QuestionService;
import ru.belov.ourabroad.api.usecases.services.reputation.ReputationService;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.core.domain.Answer;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Question;
import ru.belov.ourabroad.core.domain.UserFactory;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.FieldValidator;
import ru.belov.ourabroad.web.validators.UserValidator;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                AnswerQuestionUseCaseImpl.class,
                UserValidator.class,
                FieldValidator.class
        }
)
class AnswerQuestionUseCaseImplTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private QuestionService questionService;

    @MockitoBean
    private AnswerService answerService;

    @MockitoBean
    private ReputationService reputationService;

    @Autowired
    private AnswerQuestionUseCase useCase;

    @Captor
    private ArgumentCaptor<Answer> answerCaptor;

    private static final String QUESTION_ID = "q-1";
    private static final String AUTHOR_ID = "user-answerer";
    private static final String CONTENT = "Попробуйте так...";

    @Test
    void contextCreated() {
        assertNotNull(useCase);
    }

    @Test
    void WHEN_questionNotFound_THEN_error() {
        when(userService.findById(eq(AUTHOR_ID), any(Context.class)))
                .thenReturn(UserFactory.newUser(AUTHOR_ID, "a@b.c", "+79001112233", "Secret1", null, null, null));
        when(questionService.findById(eq(QUESTION_ID), any(Context.class))).thenAnswer(invocation -> {
            Context ctx = invocation.getArgument(1);
            ctx.setError(ErrorCode.QUESTION_NOT_FOUND);
            return null;
        });

        var request = new AnswerQuestionUseCase.Request(QUESTION_ID, AUTHOR_ID, CONTENT, null);
        AnswerQuestionUseCase.Response response = useCase.execute(request);

        assertThat(response.success()).isFalse();
        assertThat(response.answerId()).isNull();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.QUESTION_NOT_FOUND.getMessage());
        verify(answerService, never()).createAnswer(any(), any());
        verify(questionService, never()).incrementAnswersCount(anyString(), any());
    }

    @Test
    void WHEN_validRequest_THEN_savesAnswerIncrementsCountAndReputation() {
        Question question = Question.create(
                QUESTION_ID, "other-user", "T", "C", Set.of(), 0, 0, null
        );
        when(userService.findById(eq(AUTHOR_ID), any(Context.class)))
                .thenReturn(UserFactory.newUser(AUTHOR_ID, "a@b.c", "+79001112233", "Secret1", null, null, null));
        when(questionService.findById(eq(QUESTION_ID), any(Context.class))).thenReturn(question);
        doNothing().when(answerService).createAnswer(any(Answer.class), any(Context.class));
        doNothing().when(questionService).incrementAnswersCount(eq(QUESTION_ID), any(Context.class));
        doNothing().when(reputationService).addPoints(anyString(), anyInt(), any(Context.class));

        var request = new AnswerQuestionUseCase.Request(QUESTION_ID, AUTHOR_ID, CONTENT, null);
        AnswerQuestionUseCase.Response response = useCase.execute(request);

        assertThat(response.success()).isTrue();
        assertThat(response.answerId()).isNotNull();
        assertThat(response.questionId()).isEqualTo(QUESTION_ID);
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());

        verify(answerService).createAnswer(answerCaptor.capture(), any(Context.class));
        assertThat(answerCaptor.getValue().getQuestionId()).isEqualTo(QUESTION_ID);
        assertThat(answerCaptor.getValue().getAuthorId()).isEqualTo(AUTHOR_ID);

        verify(questionService).incrementAnswersCount(eq(QUESTION_ID), any(Context.class));
        verify(reputationService).addPoints(
                eq(AUTHOR_ID),
                eq(QaReputationRules.POINTS_NEW_ANSWER),
                any(Context.class)
        );
    }
}

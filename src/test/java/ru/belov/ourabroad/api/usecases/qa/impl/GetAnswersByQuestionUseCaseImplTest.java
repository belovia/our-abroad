package ru.belov.ourabroad.api.usecases.qa.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.qa.GetAnswersByQuestionUseCase;
import ru.belov.ourabroad.api.usecases.services.qa.AnswerService;
import ru.belov.ourabroad.api.usecases.services.qa.QuestionService;
import ru.belov.ourabroad.core.domain.Answer;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Question;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.FieldValidator;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                GetAnswersByQuestionUseCaseImpl.class,
                FieldValidator.class
        }
)
class GetAnswersByQuestionUseCaseImplTest {

    @MockitoBean
    private QuestionService questionService;

    @MockitoBean
    private AnswerService answerService;

    @Autowired
    private GetAnswersByQuestionUseCase useCase;

    @Test
    void contextCreated() {
        assertNotNull(useCase);
    }

    @Test
    void WHEN_validQuestion_THEN_returnsSortedAnswers() {
        Question q = Question.create("q1", "u1", "t", "c", Set.of(), 0, 0, null);
        when(questionService.findByIdOrError(eq("q1"), any(Context.class))).thenReturn(q);
        when(answerService.getAnswersSorted(eq("q1"), any(Context.class))).thenReturn(List.of(
                Answer.create("a1", "q1", "u2", null, "x", 1, false, null)
        ));

        GetAnswersByQuestionUseCase.Response response = useCase.execute(new GetAnswersByQuestionUseCase.Request("q1"));

        assertThat(response.success()).isTrue();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
        assertThat(response.answers()).hasSize(1);
        assertThat(response.answers().getFirst().id()).isEqualTo("a1");
        verify(answerService).getAnswersSorted(eq("q1"), any(Context.class));
    }

    @Test
    void WHEN_questionMissing_THEN_errorAndNoAnswersLoad() {
        when(questionService.findByIdOrError(eq("missing"), any(Context.class))).thenAnswer(invocation -> {
            Context ctx = invocation.getArgument(1);
            ctx.setError(ErrorCode.QUESTION_NOT_FOUND);
            return null;
        });

        GetAnswersByQuestionUseCase.Response response = useCase.execute(new GetAnswersByQuestionUseCase.Request("missing"));

        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.QUESTION_NOT_FOUND.getMessage());
        verify(answerService, never()).getAnswersSorted(any(), any());
    }
}


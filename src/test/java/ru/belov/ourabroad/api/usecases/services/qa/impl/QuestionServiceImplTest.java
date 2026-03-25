package ru.belov.ourabroad.api.usecases.services.qa.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.services.qa.QuestionService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.domain.Question;
import ru.belov.ourabroad.poi.storage.QuestionRepository;
import ru.belov.ourabroad.web.validators.ErrorCode;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = QuestionServiceImpl.class)
class QuestionServiceImplTest {

    @MockitoBean
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionService questionService;

    @Test
    void contextCreated() {
        assertNotNull(questionService);
    }

    @Test
    void WHEN_findById_missing_THEN_contextErrorAndNull() {
        when(questionRepository.findById("missing")).thenReturn(Optional.empty());
        Context context = new Context();

        Question q = questionService.findById("missing", context);

        assertThat(q).isNull();
        assertThat(context.isSuccess()).isFalse();
        assertThat(context.getErrorCode()).isEqualTo(ErrorCode.QUESTION_NOT_FOUND);
    }

    @Test
    void WHEN_findById_present_THEN_returnsQuestion() {
        Question expected = Question.create(
                "q1", "u1", "t", "c", Set.of(), 1, 2, null
        );
        when(questionRepository.findById("q1")).thenReturn(Optional.of(expected));
        Context context = new Context();

        Question q = questionService.findById("q1", context);

        assertThat(q).isEqualTo(expected);
        assertThat(context.isSuccess()).isTrue();
    }

    @Test
    void WHEN_createQuestion_THEN_repositorySave() {
        Question q = Question.create("q1", "u1", "t", "c", Set.of(), 0, 0, null);
        Context context = new Context();

        questionService.createQuestion(q, context);

        verify(questionRepository).save(q);
        assertThat(context.isSuccess()).isTrue();
    }
}

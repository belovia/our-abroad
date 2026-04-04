package ru.belov.ourabroad.api.usecases.qa.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.qa.GetQuestionsUseCase;
import ru.belov.ourabroad.api.usecases.services.qa.QuestionService;
import ru.belov.ourabroad.core.domain.Question;
import ru.belov.ourabroad.web.validators.ErrorCode;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = GetQuestionsUseCaseImpl.class)
class GetQuestionsUseCaseImplTest {

    @MockitoBean
    private QuestionService questionService;

    @Autowired
    private GetQuestionsUseCase useCase;

    @Test
    void contextCreated() {
        assertNotNull(useCase);
    }

    @Test
    void WHEN_paginationRequested_THEN_passesPageable() {
        when(questionService.findQuestionsPage(any(Pageable.class), any(Sort.class))).thenReturn(List.of());

        GetQuestionsUseCase.Response response = useCase.execute(
                new GetQuestionsUseCase.Request(1, 10, GetQuestionsUseCase.SortMode.NEWEST, null)
        );

        assertThat(response.success()).isTrue();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(questionService).findQuestionsPage(pageableCaptor.capture(), any(Sort.class));
        assertThat(pageableCaptor.getValue().isPaged()).isTrue();
        assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(1);
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(10);
    }

    @Test
    void WHEN_tagProvided_THEN_usesFindByTag() {
        Question q = Question.create("q1", "u1", "t", "c", Set.of("java"), 0, 0, null);
        when(questionService.findQuestionsByTag(eq("java"), any(Pageable.class), any(Sort.class)))
                .thenReturn(List.of(q));

        GetQuestionsUseCase.Response response = useCase.execute(
                new GetQuestionsUseCase.Request(0, 20, GetQuestionsUseCase.SortMode.VOTES, "java")
        );

        assertThat(response.success()).isTrue();
        assertThat(response.questions()).hasSize(1);
        assertThat(response.questions().getFirst().id()).isEqualTo("q1");
        verify(questionService, never()).findQuestionsPage(any(), any());
        verify(questionService).findQuestionsByTag(eq("java"), any(Pageable.class), any(Sort.class));
    }
}


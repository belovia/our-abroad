package ru.belov.ourabroad.api.usecases.qa.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.qa.CreateQuestionUseCase;
import ru.belov.ourabroad.api.usecases.qa.QaReputationRules;
import ru.belov.ourabroad.api.usecases.services.qa.QuestionService;
import ru.belov.ourabroad.api.usecases.services.reputation.ReputationService;
import ru.belov.ourabroad.api.usecases.services.user.UserService;
import ru.belov.ourabroad.config.security.CurrentUserProvider;
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
                CreateQuestionUseCaseImpl.class,
                UserValidator.class,
                FieldValidator.class
        }
)
class CreateQuestionUseCaseImplTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private QuestionService questionService;

    @MockitoBean
    private ReputationService reputationService;

    @MockitoBean
    private CurrentUserProvider currentUserProvider;

    @Autowired
    private CreateQuestionUseCase useCase;

    @Captor
    private ArgumentCaptor<Question> questionCaptor;

    private static final String AUTHOR_ID = "user-author";
    private static final String TITLE = "Как настроить Spring?";
    private static final String CONTENT = "Подробное описание проблемы.";

    @BeforeEach
    void stubAuthor() {
        when(currentUserProvider.requiredUserId()).thenReturn(AUTHOR_ID);
    }

    @Test
    void contextCreated() {
        assertNotNull(useCase);
    }

    @Test
    void WHEN_validRequest_THEN_savesQuestionAndGrantsReputation() {
        when(userService.findById(eq(AUTHOR_ID), any(Context.class)))
                .thenReturn(UserFactory.newUser(AUTHOR_ID, "a@b.c", "+79001112233", "Secret1", null, null, null));
        doNothing().when(questionService).createQuestion(any(Question.class), any(Context.class));
        doNothing().when(reputationService).addPoints(anyString(), anyInt(), any(Context.class));

        var request = new CreateQuestionUseCase.Request(
                TITLE, CONTENT, Set.of("java", "spring")
        );
        CreateQuestionUseCase.Response response = useCase.execute(request);

        assertThat(response.success()).isTrue();
        assertThat(response.questionId()).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());

        verify(questionService).createQuestion(questionCaptor.capture(), any(Context.class));
        Question saved = questionCaptor.getValue();
        assertThat(saved.getAuthorId()).isEqualTo(AUTHOR_ID);
        assertThat(saved.getTitle()).isEqualTo(TITLE);
        assertThat(saved.getContent()).isEqualTo(CONTENT);
        assertThat(saved.getTags()).containsExactlyInAnyOrder("java", "spring");

        verify(reputationService).addPoints(
                eq(AUTHOR_ID),
                eq(QaReputationRules.POINTS_NEW_QUESTION),
                any(Context.class)
        );
    }

    @Test
    void WHEN_titleMissing_THEN_validationError() {
        var request = new CreateQuestionUseCase.Request(null, CONTENT, Set.of());

        CreateQuestionUseCase.Response response = useCase.execute(request);

        assertThat(response.success()).isFalse();
        assertThat(response.questionId()).isNull();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.FIELD_REQUIRED.getMessage());
        verify(questionService, never()).createQuestion(any(), any());
        verify(reputationService, never()).addPoints(anyString(), anyInt(), any());
    }
}

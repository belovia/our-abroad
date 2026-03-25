package ru.belov.ourabroad.api.usecases.qa.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.belov.ourabroad.api.usecases.qa.VoteUseCase;
import ru.belov.ourabroad.api.usecases.services.qa.VoteApplyResult;
import ru.belov.ourabroad.api.usecases.services.qa.VoteService;
import ru.belov.ourabroad.api.usecases.services.reputation.ReputationService;
import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.enums.QaVoteTarget;
import ru.belov.ourabroad.core.enums.VoteType;
import ru.belov.ourabroad.web.validators.ErrorCode;
import ru.belov.ourabroad.web.validators.FieldValidator;
import ru.belov.ourabroad.web.validators.UserValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                VoteUseCaseImpl.class,
                UserValidator.class,
                FieldValidator.class
        }
)
class VoteUseCaseImplTest {

    @MockitoBean
    private VoteService voteService;

    @MockitoBean
    private ReputationService reputationService;

    @Autowired
    private VoteUseCase useCase;

    private static final String VOTER_ID = "voter-1";
    private static final String ENTITY_ID = "q-or-a-1";
    private static final String AUTHOR_ID = "content-author";

    @Test
    void contextCreated() {
        assertNotNull(useCase);
    }

    @Test
    void WHEN_firstUpvoteOnQuestion_THEN_appliesReputationToAuthor() {
        when(voteService.voteQuestion(eq(VOTER_ID), eq(ENTITY_ID), eq(VoteType.UP), any(Context.class)))
                .thenReturn(new VoteApplyResult(AUTHOR_ID, 1));
        doNothing().when(reputationService).addPoints(anyString(), anyInt(), any(Context.class));

        var request = new VoteUseCase.Request(VOTER_ID, QaVoteTarget.QUESTION, ENTITY_ID, VoteType.UP);
        VoteUseCase.Response response = useCase.execute(request);

        assertThat(response.success()).isTrue();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
        verify(reputationService).addPoints(eq(AUTHOR_ID), eq(1), any(Context.class));
    }

    @Test
    void WHEN_sameVoteNoop_THEN_reputationDeltaZeroStillCalled() {
        when(voteService.voteAnswer(eq(VOTER_ID), eq(ENTITY_ID), eq(VoteType.DOWN), any(Context.class)))
                .thenReturn(new VoteApplyResult(AUTHOR_ID, 0));
        doNothing().when(reputationService).addPoints(anyString(), anyInt(), any(Context.class));

        var request = new VoteUseCase.Request(VOTER_ID, QaVoteTarget.ANSWER, ENTITY_ID, VoteType.DOWN);
        VoteUseCase.Response response = useCase.execute(request);

        assertThat(response.success()).isTrue();
        verify(reputationService).addPoints(eq(AUTHOR_ID), eq(0), any(Context.class));
    }

    @Test
    void WHEN_voteServiceReturnsNull_THEN_errorAndNoReputationChange() {
        when(voteService.voteQuestion(eq(VOTER_ID), eq(ENTITY_ID), eq(VoteType.UP), any(Context.class)))
                .thenAnswer(invocation -> {
                    Context ctx = invocation.getArgument(3);
                    ctx.setError(ErrorCode.QUESTION_NOT_FOUND);
                    return null;
                });

        var request = new VoteUseCase.Request(VOTER_ID, QaVoteTarget.QUESTION, ENTITY_ID, VoteType.UP);
        VoteUseCase.Response response = useCase.execute(request);

        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isEqualTo(ErrorCode.QUESTION_NOT_FOUND.getMessage());
        verify(reputationService, never()).addPoints(anyString(), anyInt(), any());
    }

    @Test
    void WHEN_switchToOppositeVote_THEN_negativeReputationDelta() {
        when(voteService.voteQuestion(eq(VOTER_ID), eq(ENTITY_ID), eq(VoteType.DOWN), any(Context.class)))
                .thenReturn(new VoteApplyResult(AUTHOR_ID, -1));
        doNothing().when(reputationService).addPoints(anyString(), anyInt(), any(Context.class));

        var request = new VoteUseCase.Request(VOTER_ID, QaVoteTarget.QUESTION, ENTITY_ID, VoteType.DOWN);
        VoteUseCase.Response response = useCase.execute(request);

        assertThat(response.success()).isTrue();
        verify(reputationService).addPoints(eq(AUTHOR_ID), eq(-1), any(Context.class));
    }
}

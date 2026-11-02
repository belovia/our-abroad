package ru.belov.ourabroad.api.usecases.qa;

import ru.belov.ourabroad.core.enums.QaVoteTarget;
import ru.belov.ourabroad.core.enums.VoteType;

public interface VoteUseCase {

    Response execute(Request request);

    record Request(
            QaVoteTarget target,
            String entityId,
            VoteType voteType
    ) {
    }

    record Response(boolean success, String errorMessage) {
    }
}

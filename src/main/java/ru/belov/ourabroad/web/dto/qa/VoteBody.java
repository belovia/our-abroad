package ru.belov.ourabroad.web.dto.qa;

import ru.belov.ourabroad.core.enums.QaVoteTarget;
import ru.belov.ourabroad.core.enums.VoteType;

public record VoteBody(
        String voterUserId,
        QaVoteTarget target,
        String entityId,
        VoteType voteType
) {
}

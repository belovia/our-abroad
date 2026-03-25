package ru.belov.ourabroad.api.usecases.services.qa;

import ru.belov.ourabroad.core.domain.Context;
import ru.belov.ourabroad.core.enums.VoteType;

public interface VoteService {

    VoteApplyResult voteQuestion(String voterUserId, String questionId, VoteType voteType, Context context);

    VoteApplyResult voteAnswer(String voterUserId, String answerId, VoteType voteType, Context context);
}

package ru.belov.ourabroad.core.domain;

import ru.belov.ourabroad.core.domain.Reputation;

public final class ReputationFactory {

    private ReputationFactory() {}

    public static Reputation fromDb(
            String userId,
            int score,
            int level
    ) {
        return Reputation.create(
                userId,
                score,
                level
        );
    }

    public static Reputation initial(String userId) {
        return Reputation.create(
                userId,
                0,
                1
        );
    }
}
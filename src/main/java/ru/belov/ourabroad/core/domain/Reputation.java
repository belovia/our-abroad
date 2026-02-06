package ru.belov.ourabroad.core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class Reputation {

    private final String userId;
    private int score;
    private int level;

    private Reputation(String userId) {
        this.userId = userId;
    }

    public static Reputation create(
            String userId,
            int score,
            int level
    ) {
        Objects.requireNonNull(userId, "userId must not be null");

        Reputation rep = new Reputation(userId);
        rep.score = score;
        rep.level = level;
        return rep;
    }
}
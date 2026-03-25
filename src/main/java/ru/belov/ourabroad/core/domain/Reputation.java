package ru.belov.ourabroad.core.domain;

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

    /**
     * Увеличивает счёт очков и пересчитывает уровень (1 уровень на каждые 100 очков, макс. 10).
     */
    public void addPoints(int positiveDelta) {
        if (positiveDelta <= 0) {
            throw new IllegalArgumentException("positiveDelta must be > 0");
        }
        this.score += positiveDelta;
        this.level = levelForScore(this.score);
    }

    public static int levelForScore(int score) {
        if (score < 0) {
            return 1;
        }
        int computed = 1 + score / 100;
        return Math.min(10, Math.max(1, computed));
    }
}
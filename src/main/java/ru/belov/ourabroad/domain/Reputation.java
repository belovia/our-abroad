package ru.belov.ourabroad.domain;

public class Reputation {


    private final String userId;
    private int score;
    private int level;


    public Reputation(String userId) {
        this.userId = userId;
        this.score = 0;
        this.level = 1;
    }


    public void applyDelta(int delta) {
        this.score += delta;
        recalcLevel();
    }


    private void recalcLevel() {
        this.level = Math.max(1, score / 100);
    }


// getters
}
package ru.belov.ourabroad.core.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReputationTest {

    @Test
    void levelForScore_mapsTiersAndCapsAtTen() {
        assertThat(Reputation.levelForScore(0)).isEqualTo(1);
        assertThat(Reputation.levelForScore(99)).isEqualTo(1);
        assertThat(Reputation.levelForScore(100)).isEqualTo(2);
        assertThat(Reputation.levelForScore(900)).isEqualTo(10);
        assertThat(Reputation.levelForScore(10_000)).isEqualTo(10);
    }

    @Test
    void addPoints_incrementsScoreAndRecalculatesLevel() {
        Reputation r = Reputation.create("u1", 50, 1);
        r.addPoints(60);
        assertThat(r.getScore()).isEqualTo(110);
        assertThat(r.getLevel()).isEqualTo(2);
    }

    @Test
    void addPoints_rejectsNonPositiveDelta() {
        Reputation r = Reputation.create("u1", 10, 1);
        assertThatThrownBy(() -> r.addPoints(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> r.addPoints(-1)).isInstanceOf(IllegalArgumentException.class);
    }
}

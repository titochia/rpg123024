package it.unicam.cs.mpgc.rpg123024.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StatsTest {

    @Test
    void rejectsNonPositiveMaxHealth() {
        assertThrows(IllegalArgumentException.class, () -> new Stats(0, 1, 1, 1));
    }

    @Test
    void rejectsNegativeAttackDefenseOrSpeed() {
        assertThrows(IllegalArgumentException.class, () -> new Stats(10, -1, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new Stats(10, 0, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Stats(10, 0, 0, -1));
    }

    @Test
    void plusSumsFieldByField() {
        Stats result = new Stats(10, 2, 3, 4).plus(new Stats(5, 1, 1, 1));

        assertEquals(new Stats(15, 3, 4, 5), result);
    }
}
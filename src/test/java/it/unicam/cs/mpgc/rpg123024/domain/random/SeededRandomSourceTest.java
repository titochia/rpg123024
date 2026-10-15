package it.unicam.cs.mpgc.rpg123024.domain.random;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeededRandomSourceTest {

    @Test
    void sameSeedProducesSameSequence() {
        RandomSource first = new SeededRandomSource(42);
        RandomSource second = new SeededRandomSource(42);

        for (int i = 0; i < 20; i++) {
            assertEquals(first.nextInt(1000), second.nextInt(1000));
        }
    }

    @Test
    void chanceHandlesExtremeProbabilities() {
        RandomSource random = new SeededRandomSource(1);

        for (int i = 0; i < 50; i++) {
            assertFalse(random.chance(0.0));
            assertTrue(random.chance(1.0));
        }
    }
}
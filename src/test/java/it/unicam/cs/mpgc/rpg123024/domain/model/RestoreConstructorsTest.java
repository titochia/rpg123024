package it.unicam.cs.mpgc.rpg123024.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RestoreConstructorsTest {

    private static final Stats STATS = new Stats(100, 10, 2, 5);

    @Test
    void monsterRestoresCurrentHealth() {
        Monster monster = new Monster("Orc", STATS, 10, 40);

        assertEquals(40, monster.currentHealth());
    }

    @Test
    void monsterRejectsCurrentHealthOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> new Monster("Orc", STATS, 10, -1));
        assertThrows(IllegalArgumentException.class, () -> new Monster("Orc", STATS, 10, 101));
    }

    @Test
    void heroRestoresProgressAndHealth() {
        Hero hero = new Hero("Aria", STATS, 5, 3, 50, 70);

        assertEquals(3, hero.level());
        assertEquals(50, hero.experience());
        assertEquals(70, hero.currentHealth());
        assertEquals(5, hero.inventory().capacity());
    }

    @Test
    void heroRejectsInvalidRestoreData() {
        assertThrows(IllegalArgumentException.class, () -> new Hero("Aria", STATS, 5, 0, 0, 100));
        assertThrows(IllegalArgumentException.class, () -> new Hero("Aria", STATS, 5, 1, -1, 100));
        assertThrows(IllegalArgumentException.class, () -> new Hero("Aria", STATS, 5, 1, 0, 101));
    }
}

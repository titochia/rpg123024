package it.unicam.cs.mpgc.rpg123024.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatantTest {

    private Combatant combatant;

    @BeforeEach
    void setUp() {
        combatant = new Monster("Goblin", new Stats(50, 5, 2, 3), 20);
    }

    @Test
    void startsAtFullHealth() {
        assertEquals(50, combatant.currentHealth());
        assertTrue(combatant.isAlive());
    }

    @Test
    void takeDamageReducesHealthAndReturnsApplied() {
        int applied = combatant.takeDamage(20);

        assertEquals(20, applied);
        assertEquals(30, combatant.currentHealth());
    }

    @Test
    void takeDamageIsClampedAtZeroAndKillsCombatant() {
        int applied = combatant.takeDamage(999);

        assertEquals(50, applied);
        assertEquals(0, combatant.currentHealth());
        assertFalse(combatant.isAlive());
    }

    @Test
    void healIsClampedAtMaxHealth() {
        combatant.takeDamage(10);

        int healed = combatant.heal(999);

        assertEquals(10, healed);
        assertEquals(50, combatant.currentHealth());
    }

    @Test
    void negativeAmountsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> combatant.takeDamage(-1));
        assertThrows(IllegalArgumentException.class, () -> combatant.heal(-1));
    }
}
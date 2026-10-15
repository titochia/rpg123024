package it.unicam.cs.mpgc.rpg123024.domain.effect;

import it.unicam.cs.mpgc.rpg123024.domain.model.Combatant;
import it.unicam.cs.mpgc.rpg123024.domain.model.Monster;
import it.unicam.cs.mpgc.rpg123024.domain.model.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatusEffectTest {

    private Combatant base;

    @BeforeEach
    void setUp() {
        base = new Monster("Target", new Stats(50, 5, 4, 1), 0);
    }

    @Test
    void shieldRaisesDefenseOnlyWhileActive() {
        ShieldEffect shield = new ShieldEffect(base, 3, 1);

        assertEquals(7, shield.stats().defense());

        shield.onTurnStart();

        assertTrue(shield.isExpired());
        assertEquals(4, shield.stats().defense());
    }

    @Test
    void poisonDamagesEachTurnUntilExpired() {
        PoisonEffect poison = new PoisonEffect(base, 5, 2);

        poison.onTurnStart();
        poison.onTurnStart();
        poison.onTurnStart();

        assertEquals(40, base.currentHealth());
        assertEquals(0, poison.remainingTurns());
        assertTrue(poison.isExpired());
    }

    @Test
    void decoratorSharesStateWithDecoratedCombatant() {
        ShieldEffect shield = new ShieldEffect(base, 3, 2);

        shield.takeDamage(10);

        assertEquals(40, base.currentHealth());
        assertEquals(40, shield.currentHealth());
        assertSame(base, shield.unwrap());
    }

    @Test
    void nestedEffectsTickTogether() {
        ShieldEffect shield = new ShieldEffect(base, 3, 3);
        PoisonEffect poison = new PoisonEffect(shield, 5, 3);

        poison.onTurnStart();

        assertEquals(45, base.currentHealth());
        assertEquals(2, shield.remainingTurns());
        assertEquals(2, poison.remainingTurns());
        assertEquals(7, poison.stats().defense());
    }

    @Test
    void expiredInnerEffectIsInertInsideActiveOuterEffect() {
        ShieldEffect shield = new ShieldEffect(base, 3, 1);
        PoisonEffect poison = new PoisonEffect(shield, 5, 3);

        poison.onTurnStart();
        poison.onTurnStart();

        assertTrue(shield.isExpired());
        assertFalse(poison.isExpired());
        assertEquals(4, poison.stats().defense());
    }

    @Test
    void rejectsInvalidParameters() {
        assertThrows(IllegalArgumentException.class, () -> new ShieldEffect(base, 3, 0));
        assertThrows(IllegalArgumentException.class, () -> new ShieldEffect(base, 0, 2));
        assertThrows(IllegalArgumentException.class, () -> new PoisonEffect(base, 0, 2));
    }
}
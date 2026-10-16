package it.unicam.cs.mpgc.rpg123024.domain.combat;

import it.unicam.cs.mpgc.rpg123024.domain.effect.PoisonEffect;
import it.unicam.cs.mpgc.rpg123024.domain.effect.ShieldEffect;
import it.unicam.cs.mpgc.rpg123024.domain.model.Combatant;
import it.unicam.cs.mpgc.rpg123024.domain.model.Monster;
import it.unicam.cs.mpgc.rpg123024.domain.model.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

class FighterTest {

    private Combatant base;
    private Fighter fighter;

    @BeforeEach
    void setUp() {
        base = new Monster("Target", new Stats(50, 5, 4, 1), 0);
        fighter = new Fighter(base);
    }

    @Test
    void applyEffectWrapsCurrentCombatant() {
        fighter.applyEffect(c -> new ShieldEffect(c, 3, 2));

        assertInstanceOf(ShieldEffect.class, fighter.combatant());
        assertEquals(7, fighter.combatant().stats().defense());
    }

    @Test
    void startTurnReturnsHealthLostToPeriodicEffects() {
        fighter.applyEffect(c -> new PoisonEffect(c, 5, 2));

        int lost = fighter.startTurn();

        assertEquals(5, lost);
        assertEquals(45, base.currentHealth());
    }

    @Test
    void startTurnRemovesExpiredOuterEffect() {
        fighter.applyEffect(c -> new ShieldEffect(c, 3, 1));

        int lost = fighter.startTurn();

        assertEquals(0, lost);
        assertSame(base, fighter.combatant());
    }

    @Test
    void startTurnWithoutEffectsLosesNothing() {
        assertEquals(0, fighter.startTurn());
        assertSame(base, fighter.combatant());
    }
}
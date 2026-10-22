package it.unicam.cs.mpgc.rpg123024.domain.combat;

import it.unicam.cs.mpgc.rpg123024.domain.ability.HealAbility;
import it.unicam.cs.mpgc.rpg123024.domain.ability.PhysicalAttack;
import it.unicam.cs.mpgc.rpg123024.domain.model.Hero;
import it.unicam.cs.mpgc.rpg123024.domain.model.Monster;
import it.unicam.cs.mpgc.rpg123024.domain.model.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CooldownActionTest {

    private final CooldownAction heal = new CooldownAction("Heal",
            new AbilityAction(new HealAbility("Heal", 20), Targeting.SELF), 2);

    private Hero hero;
    private Fighter actor;
    private Fighter opponent;

    @BeforeEach
    void setUp() {
        hero = new Hero("Aria", new Stats(100, 10, 2, 5));
        hero.takeDamage(50);
        actor = new Fighter(hero);
        opponent = new Fighter(new Monster("Orc", new Stats(50, 8, 1, 3), 10));
    }

    @Test
    void executesWrappedActionAndStartsRecharging() {
        ActionResult result = heal.execute(actor, opponent);

        assertEquals(20, result.amount());
        assertEquals(70, hero.currentHealth());
        assertFalse(heal.isAvailableFor(actor.combatant()));
    }

    @Test
    void becomesAvailableAgainAfterConfiguredTurns() {
        heal.execute(actor, opponent);

        actor.startTurn();
        assertFalse(heal.isAvailableFor(actor.combatant()));

        actor.startTurn();
        assertTrue(heal.isAvailableFor(actor.combatant()));
    }

    @Test
    void executingWhileRechargingIsRejected() {
        heal.execute(actor, opponent);

        assertThrows(IllegalStateException.class, () -> heal.execute(actor, opponent));
        assertEquals(70, hero.currentHealth());
    }

    @Test
    void cooldownsOfDifferentActionsAreIndependent() {
        CooldownAction strike = new CooldownAction("Strike",
                new AbilityAction(new PhysicalAttack(), Targeting.OPPONENT), 2);

        heal.execute(actor, opponent);

        assertTrue(strike.isAvailableFor(actor.combatant()));
    }

    @Test
    void sameInstanceIsAvailableForANewFighter() {
        heal.execute(actor, opponent);

        assertTrue(heal.isAvailableFor(new Fighter(hero).combatant()));
    }

    @Test
    void nonPositiveDurationIsRejected() {
        TurnAction attack = new AbilityAction(new PhysicalAttack(), Targeting.OPPONENT);

        assertThrows(IllegalArgumentException.class, () -> new CooldownAction("Strike", attack, 0));
    }
}

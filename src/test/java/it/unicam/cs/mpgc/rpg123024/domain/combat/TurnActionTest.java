package it.unicam.cs.mpgc.rpg123024.domain.combat;

import it.unicam.cs.mpgc.rpg123024.domain.ability.HealAbility;
import it.unicam.cs.mpgc.rpg123024.domain.ability.PhysicalAttack;
import it.unicam.cs.mpgc.rpg123024.domain.effect.PoisonEffect;
import it.unicam.cs.mpgc.rpg123024.domain.model.Combatant;
import it.unicam.cs.mpgc.rpg123024.domain.model.Hero;
import it.unicam.cs.mpgc.rpg123024.domain.model.Item;
import it.unicam.cs.mpgc.rpg123024.domain.model.Monster;
import it.unicam.cs.mpgc.rpg123024.domain.model.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TurnActionTest {

    private Combatant actorBase;
    private Combatant opponentBase;
    private Fighter actor;
    private Fighter opponent;

    @BeforeEach
    void setUp() {
        actorBase = new Monster("Actor", new Stats(50, 10, 0, 1), 0);
        opponentBase = new Monster("Opponent", new Stats(50, 5, 4, 1), 0);
        actor = new Fighter(actorBase);
        opponent = new Fighter(opponentBase);
    }

    @Test
    void abilityActionOnOpponentDamagesOpponent() {
        TurnAction action = new AbilityAction(new PhysicalAttack(), Targeting.OPPONENT);

        ActionResult result = action.execute(actor, opponent);

        assertEquals(new ActionResult("Actor", "Attack", "Opponent", 6), result);
        assertEquals(44, opponentBase.currentHealth());
    }

    @Test
    void abilityActionOnSelfTargetsActor() {
        actorBase.takeDamage(20);
        TurnAction action = new AbilityAction(new HealAbility("Heal", 30), Targeting.SELF);

        ActionResult result = action.execute(actor, opponent);

        assertEquals(new ActionResult("Actor", "Heal", "Actor", 20), result);
        assertEquals(50, actorBase.currentHealth());
    }

    @Test
    void applyEffectActionWrapsOnlyTheRecipient() {
        TurnAction action = new ApplyEffectAction("Poison", c -> new PoisonEffect(c, 3, 2), Targeting.OPPONENT);

        ActionResult result = action.execute(actor, opponent);

        assertInstanceOf(PoisonEffect.class, opponent.combatant());
        assertSame(actorBase, actor.combatant());
        assertEquals(new ActionResult("Actor", "Poison", "Opponent", 0), result);
    }

    @Test
    void useItemActionConsumesItemAndReportsHealing() {
        Hero hero = new Hero("Hero", new Stats(100, 10, 2, 5));
        Item potion = new Item("potion", "Potion", "d", target -> target.heal(30));
        hero.inventory().add(potion);
        hero.takeDamage(40);

        ActionResult result = new UseItemAction(hero, potion).execute(new Fighter(hero), opponent);

        assertEquals(new ActionResult("Hero", "Potion", "Hero", 30), result);
        assertEquals(0, hero.inventory().size());
    }

    @Test
    void useItemActionFailsWhenItemIsNotInInventory() {
        Hero hero = new Hero("Hero", new Stats(100, 10, 2, 5));
        Item potion = new Item("potion", "Potion", "d", target -> target.heal(30));
        UseItemAction action = new UseItemAction(hero, potion);
        Fighter heroFighter = new Fighter(hero);

        assertThrows(IllegalStateException.class, () -> action.execute(heroFighter, opponent));
    }
}
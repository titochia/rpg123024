package it.unicam.cs.mpgc.rpg123024.domain.combat;

import it.unicam.cs.mpgc.rpg123024.domain.ability.PhysicalAttack;
import it.unicam.cs.mpgc.rpg123024.domain.effect.PoisonEffect;
import it.unicam.cs.mpgc.rpg123024.domain.model.Hero;
import it.unicam.cs.mpgc.rpg123024.domain.model.Monster;
import it.unicam.cs.mpgc.rpg123024.domain.model.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatEngineTest {

    private static final TurnAction ATTACK = new AbilityAction(new PhysicalAttack(), Targeting.OPPONENT);
    private static final TurnAction POISON =
            new ApplyEffectAction("Poison", c -> new PoisonEffect(c, 5, 3), Targeting.OPPONENT);

    private Hero hero;

    @BeforeEach
    void setUp() {
        hero = new Hero("Hero", new Stats(100, 10, 2, 5));
    }

    private CombatEngine engineAgainst(Monster monster) {
        return new CombatEngine(hero, monster, new AggressiveMonsterAI());
    }

    private static Monster orc(int health, int speed) {
        return new Monster("Orc", new Stats(health, 6, 1, speed), 50);
    }

    @Test
    void fasterCombatantActsFirst() {
        assertTrue(engineAgainst(orc(30, 3)).isHeroTurn());
        assertFalse(engineAgainst(orc(30, 9)).isHeroTurn());
    }

    @Test
    void turnsAlternate() {
        CombatEngine engine = engineAgainst(orc(30, 3));

        engine.heroAct(ATTACK);
        assertFalse(engine.isHeroTurn());

        engine.monsterAct();
        assertTrue(engine.isHeroTurn());
    }

    @Test
    void actingOutOfTurnIsRejected() {
        CombatEngine engine = engineAgainst(orc(30, 3));

        assertThrows(IllegalStateException.class, engine::monsterAct);

        engine.heroAct(ATTACK);

        assertThrows(IllegalStateException.class, () -> engine.heroAct(ATTACK));
    }

    @Test
    void heroWinsWhenMonsterDies() {
        CombatEngine engine = engineAgainst(orc(5, 3));

        engine.heroAct(ATTACK);

        assertEquals(CombatEngine.Outcome.HERO_WON, engine.outcome());
        assertTrue(engine.isOver());
        assertThrows(IllegalStateException.class, engine::monsterAct);
    }

    @Test
    void heroIsDefeatedWhenHeroDies() {
        hero.takeDamage(99);
        CombatEngine engine = engineAgainst(orc(30, 9));

        engine.monsterAct();

        assertEquals(CombatEngine.Outcome.HERO_DEFEATED, engine.outcome());
    }

    @Test
    void periodicEffectIsReportedAtStartOfAffectedTurn() {
        CombatEngine engine = engineAgainst(orc(30, 3));
        engine.heroAct(POISON);

        List<ActionResult> results = engine.monsterAct();

        assertEquals(2, results.size());
        assertEquals("Status effects", results.get(0).actionName());
        assertEquals(5, results.get(0).amount());
        assertEquals("Attack", results.get(1).actionName());
        assertEquals(25, engine.monsterCombatant().currentHealth());
    }

    @Test
    void periodicEffectKillingActorSkipsItsAction() {
        CombatEngine engine = engineAgainst(orc(4, 3));
        engine.heroAct(POISON);

        List<ActionResult> results = engine.monsterAct();

        assertEquals(1, results.size());
        assertEquals(CombatEngine.Outcome.HERO_WON, engine.outcome());
        assertEquals(100, hero.currentHealth());
    }
}
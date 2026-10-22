package it.unicam.cs.mpgc.rpg123024.application.state;

import it.unicam.cs.mpgc.rpg123024.application.Fixtures;
import it.unicam.cs.mpgc.rpg123024.application.port.EventPublisher;
import it.unicam.cs.mpgc.rpg123024.domain.ability.HealAbility;
import it.unicam.cs.mpgc.rpg123024.domain.combat.AbilityAction;
import it.unicam.cs.mpgc.rpg123024.domain.combat.CombatEngine;
import it.unicam.cs.mpgc.rpg123024.domain.combat.CooldownAction;
import it.unicam.cs.mpgc.rpg123024.domain.combat.Targeting;
import it.unicam.cs.mpgc.rpg123024.domain.combat.TurnAction;
import it.unicam.cs.mpgc.rpg123024.domain.event.GameEvent;
import it.unicam.cs.mpgc.rpg123024.domain.map.Direction;
import it.unicam.cs.mpgc.rpg123024.domain.map.Dungeon;
import it.unicam.cs.mpgc.rpg123024.domain.map.Position;
import it.unicam.cs.mpgc.rpg123024.domain.model.Hero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatPhaseCooldownTest {

    private static final TurnAction HEAL = new CooldownAction("Heal",
            new AbilityAction(new HealAbility("Heal", 20), Targeting.SELF), 2);

    private final List<GameEvent> events = new ArrayList<>();
    private final EventPublisher publisher = events::add;
    private Hero hero;
    private GameSession session;

    @BeforeEach
    void setUp() {
        hero = Fixtures.hero();
        hero.takeDamage(60);
    }

    private void act(TurnAction action) {
        session.changePhase(session.phase().heroAct(session, action, publisher));
    }

    private void move(Direction direction) {
        session.changePhase(session.phase().move(session, direction, publisher));
    }

    @Test
    void healWhileRechargingIsRejectedWithoutConsumingTheTurn() {
        Dungeon dungeon = Fixtures.dungeon();
        dungeon.room(new Position(0, 1)).placeMonster(Fixtures.monster(100, 5, 1, 10));
        session = Fixtures.session(hero, dungeon);
        move(Direction.EAST);
        act(HEAL);
        CombatEngine combat = session.combat().orElseThrow();
        int heroHealth = hero.currentHealth();
        int monsterHealth = combat.monsterCombatant().currentHealth();
        int publishedEvents = events.size();

        assertThrows(IllegalStateException.class, () -> act(HEAL));

        assertTrue(combat.isHeroTurn());
        assertEquals(heroHealth, hero.currentHealth());
        assertEquals(monsterHealth, combat.monsterCombatant().currentHealth());
        assertEquals(publishedEvents, events.size());
        assertEquals("COMBAT", session.phase().id());
    }

    @Test
    void healIsAvailableAgainAfterTwoTurns() {
        Dungeon dungeon = Fixtures.dungeon();
        dungeon.room(new Position(0, 1)).placeMonster(Fixtures.monster(100, 1, 1, 10));
        session = Fixtures.session(hero, dungeon);
        move(Direction.EAST);
        act(HEAL);
        act(Fixtures.ATTACK);
        act(Fixtures.ATTACK);
        int before = hero.currentHealth();

        act(HEAL);

        assertTrue(hero.currentHealth() > before);
    }

    @Test
    void cooldownDoesNotCarryOverToTheNextCombat() {
        Dungeon dungeon = new Dungeon(1, 3, new Position(0, 0), new Position(0, 2));
        dungeon.room(new Position(0, 1)).placeMonster(Fixtures.monster(9, 1, 1, 10));
        dungeon.room(new Position(0, 2)).placeMonster(Fixtures.monster(9, 1, 1, 10));
        session = Fixtures.session(hero, dungeon);
        move(Direction.EAST);
        act(HEAL);
        act(Fixtures.ATTACK);
        assertEquals("EXPLORING", session.phase().id());
        move(Direction.EAST);
        assertEquals("COMBAT", session.phase().id());
        int before = hero.currentHealth();

        act(HEAL);

        assertTrue(hero.currentHealth() > before);
    }
}

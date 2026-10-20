package it.unicam.cs.mpgc.rpg123024.application.state;

import it.unicam.cs.mpgc.rpg123024.application.Fixtures;
import it.unicam.cs.mpgc.rpg123024.application.port.EventPublisher;
import it.unicam.cs.mpgc.rpg123024.domain.combat.CombatEngine;
import it.unicam.cs.mpgc.rpg123024.domain.event.GameEvent;
import it.unicam.cs.mpgc.rpg123024.domain.map.Direction;
import it.unicam.cs.mpgc.rpg123024.domain.map.Dungeon;
import it.unicam.cs.mpgc.rpg123024.domain.map.Position;
import it.unicam.cs.mpgc.rpg123024.domain.model.Hero;
import it.unicam.cs.mpgc.rpg123024.domain.model.Item;
import it.unicam.cs.mpgc.rpg123024.domain.model.Monster;
import it.unicam.cs.mpgc.rpg123024.domain.model.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatPhaseTest {

    private static final Position MONSTER_ROOM = new Position(0, 1);

    private final List<GameEvent> events = new ArrayList<>();
    private final EventPublisher publisher = events::add;
    private Hero hero;
    private Dungeon dungeon;
    private GameSession session;

    @BeforeEach
    void setUp() {
        hero = Fixtures.hero();
        dungeon = Fixtures.dungeon();
    }

    private void enterCombatWith(Monster monster) {
        session = Fixtures.session(hero, dungeon);
        dungeon.room(MONSTER_ROOM).placeMonster(monster);
        session.changePhase(session.phase().move(session, Direction.EAST, publisher));
    }

    private void heroAttacks() {
        session.changePhase(session.phase().heroAct(session, Fixtures.ATTACK, publisher));
    }

    private long actionsPerformed() {
        return events.stream().filter(GameEvent.ActionPerformed.class::isInstance).count();
    }

    @Test
    void killingMonsterGrantsExperienceAndReturnsToExploring() {
        enterCombatWith(Fixtures.monster(9, 1, 1, 120));

        heroAttacks();

        assertEquals("EXPLORING", session.phase().id());
        assertTrue(dungeon.room(MONSTER_ROOM).monster().isEmpty());
        assertEquals(2, hero.level());
        assertTrue(events.contains(new GameEvent.LevelUp(2)));
        assertTrue(events.contains(new GameEvent.CombatEnded(CombatEngine.Outcome.HERO_WON)));
        assertTrue(session.combat().isEmpty());
    }

    @Test
    void killingMonsterInExitRoomWinsTheGame() {
        dungeon = Fixtures.dungeonWithExitAt(MONSTER_ROOM);
        enterCombatWith(Fixtures.monster(9, 1, 1, 10));

        heroAttacks();

        assertEquals("VICTORY", session.phase().id());
        assertTrue(events.contains(new GameEvent.GameWon()));
    }

    @Test
    void heroDefeatEndsInDefeatPhase() {
        hero = new Hero("Hero", new Stats(5, 10, 0, 5));
        enterCombatWith(Fixtures.monster(100, 50, 1, 10));

        heroAttacks();

        assertEquals("DEFEAT", session.phase().id());
        assertTrue(events.contains(new GameEvent.CombatEnded(CombatEngine.Outcome.HERO_DEFEATED)));
        assertTrue(events.contains(new GameEvent.GameOver()));
        assertTrue(session.combat().isEmpty());
    }

    @Test
    void monsterRespondsAfterHeroActionWhileCombatContinues() {
        enterCombatWith(Fixtures.monster(1000, 6, 1, 10));

        heroAttacks();

        assertEquals(2, actionsPerformed());
        assertEquals("COMBAT", session.phase().id());
        assertTrue(session.combat().get().isHeroTurn());
    }

    @Test
    void usingItemInCombatConsumesTheTurn() {
        Item potion = Fixtures.potion();
        hero.inventory().add(potion);
        hero.takeDamage(50);
        enterCombatWith(Fixtures.monster(1000, 6, 1, 10));

        session.changePhase(session.phase().useItem(session, potion, publisher));

        assertEquals(0, hero.inventory().size());
        assertEquals(76, hero.currentHealth());
        assertEquals(2, actionsPerformed());
    }

    @Test
    void movingIsNotAllowedDuringCombat() {
        enterCombatWith(Fixtures.monster(1000, 6, 1, 10));

        assertThrows(IllegalStateException.class,
                () -> session.phase().move(session, Direction.SOUTH, publisher));
    }

    @Test
    void terminalPhasesRejectEveryCommand() {
        session = Fixtures.session(hero, dungeon);

        assertThrows(IllegalStateException.class,
                () -> VictoryPhase.INSTANCE.move(session, Direction.EAST, publisher));
        assertThrows(IllegalStateException.class,
                () -> DefeatPhase.INSTANCE.heroAct(session, Fixtures.ATTACK, publisher));
        assertThrows(IllegalStateException.class,
                () -> DefeatPhase.INSTANCE.useItem(session, Fixtures.potion(), publisher));
    }
}
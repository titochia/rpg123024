package it.unicam.cs.mpgc.rpg123024.application.state;

import it.unicam.cs.mpgc.rpg123024.application.Fixtures;
import it.unicam.cs.mpgc.rpg123024.application.port.EventPublisher;
import it.unicam.cs.mpgc.rpg123024.domain.event.GameEvent;
import it.unicam.cs.mpgc.rpg123024.domain.map.Direction;
import it.unicam.cs.mpgc.rpg123024.domain.map.Dungeon;
import it.unicam.cs.mpgc.rpg123024.domain.map.Position;
import it.unicam.cs.mpgc.rpg123024.domain.model.Hero;
import it.unicam.cs.mpgc.rpg123024.domain.model.Item;
import it.unicam.cs.mpgc.rpg123024.domain.model.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExploringPhaseTest {

    private final List<GameEvent> events = new ArrayList<>();
    private final EventPublisher publisher = events::add;
    private Hero hero;
    private Dungeon dungeon;
    private GameSession session;

    @BeforeEach
    void setUp() {
        hero = Fixtures.hero();
        dungeon = Fixtures.dungeon();
        session = Fixtures.session(hero, dungeon);
    }

    private void move(Direction direction) {
        session.changePhase(session.phase().move(session, direction, publisher));
    }

    @Test
    void sessionStartsExploringAtStartWithVisitedStartRoom() {
        assertEquals(dungeon.start(), session.position());
        assertEquals("EXPLORING", session.phase().id());
        assertTrue(session.currentRoom().isVisited());
        assertTrue(session.combat().isEmpty());
    }

    @Test
    void moveUpdatesPositionMarksVisitedAndPublishesEvent() {
        move(Direction.EAST);

        assertEquals(new Position(0, 1), session.position());
        assertTrue(session.currentRoom().isVisited());
        assertTrue(events.contains(new GameEvent.RoomEntered(new Position(0, 1))));
        assertEquals("EXPLORING", session.phase().id());
    }

    @Test
    void moveOutsideGridIsRejectedAndKeepsPosition() {
        assertThrows(IllegalArgumentException.class, () -> move(Direction.NORTH));

        assertEquals(dungeon.start(), session.position());
    }

    @Test
    void moveCollectsLootIntoInventory() {
        Item potion = Fixtures.potion();
        dungeon.room(new Position(0, 1)).addLoot(potion);

        move(Direction.EAST);

        assertTrue(hero.inventory().contains(potion));
        assertTrue(dungeon.room(new Position(0, 1)).loot().isEmpty());
        assertTrue(events.contains(new GameEvent.ItemCollected(potion)));
    }

    @Test
    void lootStaysInRoomWhenInventoryIsFull() {
        hero = new Hero("Hero", new Stats(100, 10, 2, 5), 1);
        hero.inventory().add(Fixtures.potion());
        session = Fixtures.session(hero, dungeon);
        Item elixir = new Item("elixir", "Elixir", "d", target -> { });
        dungeon.room(new Position(0, 1)).addLoot(elixir);

        move(Direction.EAST);

        assertEquals(1, hero.inventory().size());
        assertEquals(List.of(elixir), dungeon.room(new Position(0, 1)).loot());
    }

    @Test
    void movingIntoMonsterRoomStartsCombat() {
        dungeon.room(new Position(0, 1)).placeMonster(Fixtures.monster(1000, 1, 1, 10));

        move(Direction.EAST);

        assertSame(CombatPhase.INSTANCE, session.phase());
        assertTrue(session.combat().isPresent());
        assertTrue(session.combat().get().isHeroTurn());
        assertTrue(events.contains(new GameEvent.CombatStarted("Orc")));
    }

    @Test
    void fasterMonsterActsBeforeTheHero() {
        dungeon.room(new Position(0, 1)).placeMonster(Fixtures.monster(1000, 6, 9, 10));

        move(Direction.EAST);

        boolean monsterActed = events.stream().anyMatch(event ->
                event instanceof GameEvent.ActionPerformed performed
                        && performed.result().actorName().equals("Orc"));
        assertTrue(monsterActed);
        assertEquals(96, hero.currentHealth());
        assertTrue(session.combat().get().isHeroTurn());
    }

    @Test
    void reachingExitWithoutMonsterWinsTheGame() {
        session = Fixtures.session(hero, Fixtures.dungeonWithExitAt(new Position(0, 1)));

        move(Direction.EAST);

        assertSame(VictoryPhase.INSTANCE, session.phase());
        assertTrue(events.contains(new GameEvent.GameWon()));
    }

    @Test
    void useItemHealsHeroAndConsumesItem() {
        Item potion = Fixtures.potion();
        hero.inventory().add(potion);
        hero.takeDamage(40);

        session.changePhase(session.phase().useItem(session, potion, publisher));

        assertEquals(90, hero.currentHealth());
        assertEquals(0, hero.inventory().size());
        assertEquals("EXPLORING", session.phase().id());
    }

    @Test
    void combatActionsAreNotAllowedWhileExploring() {
        assertThrows(IllegalStateException.class,
                () -> session.phase().heroAct(session, Fixtures.ATTACK, publisher));
    }
}
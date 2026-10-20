package it.unicam.cs.mpgc.rpg123024.application.usecase;

import it.unicam.cs.mpgc.rpg123024.application.Fixtures;
import it.unicam.cs.mpgc.rpg123024.application.port.EventPublisher;
import it.unicam.cs.mpgc.rpg123024.application.port.GameRepository;
import it.unicam.cs.mpgc.rpg123024.application.state.GameSession;
import it.unicam.cs.mpgc.rpg123024.domain.combat.AggressiveMonsterAI;
import it.unicam.cs.mpgc.rpg123024.domain.event.GameEvent;
import it.unicam.cs.mpgc.rpg123024.domain.map.Direction;
import it.unicam.cs.mpgc.rpg123024.domain.map.Dungeon;
import it.unicam.cs.mpgc.rpg123024.domain.map.Position;
import it.unicam.cs.mpgc.rpg123024.domain.model.Hero;
import it.unicam.cs.mpgc.rpg123024.domain.model.Item;
import it.unicam.cs.mpgc.rpg123024.domain.model.Stats;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UseCasesTest {

    private final List<GameEvent> events = new ArrayList<>();
    private final EventPublisher publisher = events::add;

    private static class InMemoryRepository implements GameRepository {
        private GameSession saved;

        @Override
        public void save(GameSession session) {
            saved = session;
        }

        @Override
        public Optional<GameSession> load() {
            return Optional.ofNullable(saved);
        }
    }

    @Test
    void startNewGameCreatesSessionAtDungeonStart() {
        Dungeon dungeon = Fixtures.dungeon();
        StartNewGame useCase = new StartNewGame(() -> dungeon, new AggressiveMonsterAI(), new Stats(100, 10, 2, 5));

        GameSession session = useCase.execute("Aria");

        assertEquals("Aria", session.hero().name());
        assertEquals(dungeon.start(), session.position());
        assertEquals("EXPLORING", session.phase().id());
    }

    @Test
    void moveToRoomDelegatesToCurrentPhase() {
        GameSession session = Fixtures.session(Fixtures.hero(), Fixtures.dungeonWithExitAt(new Position(0, 1)));

        new MoveToRoom(publisher).execute(session, Direction.EAST);

        assertEquals("VICTORY", session.phase().id());
    }

    @Test
    void executeTurnDelegatesToCurrentPhase() {
        Dungeon dungeon = Fixtures.dungeon();
        dungeon.room(new Position(0, 1)).placeMonster(Fixtures.monster(9, 1, 1, 10));
        GameSession session = Fixtures.session(Fixtures.hero(), dungeon);
        new MoveToRoom(publisher).execute(session, Direction.EAST);
        assertEquals("COMBAT", session.phase().id());

        new ExecuteTurn(publisher).execute(session, Fixtures.ATTACK);

        assertEquals("EXPLORING", session.phase().id());
    }

    @Test
    void useItemDelegatesToCurrentPhase() {
        Hero hero = Fixtures.hero();
        Item potion = Fixtures.potion();
        hero.inventory().add(potion);
        hero.takeDamage(40);
        GameSession session = Fixtures.session(hero, Fixtures.dungeon());

        new UseItem(publisher).execute(session, potion);

        assertEquals(90, hero.currentHealth());
    }

    @Test
    void saveAndLoadGoThroughRepository() {
        InMemoryRepository repository = new InMemoryRepository();
        GameSession session = Fixtures.session(Fixtures.hero(), Fixtures.dungeon());

        assertTrue(new LoadGame(repository).execute().isEmpty());

        new SaveGame(repository).execute(session);

        assertSame(session, new LoadGame(repository).execute().orElseThrow());
    }
}
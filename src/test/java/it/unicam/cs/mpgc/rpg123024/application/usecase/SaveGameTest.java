package it.unicam.cs.mpgc.rpg123024.application.usecase;

import it.unicam.cs.mpgc.rpg123024.application.Fixtures;
import it.unicam.cs.mpgc.rpg123024.application.port.EventPublisher;
import it.unicam.cs.mpgc.rpg123024.application.port.GameRepository;
import it.unicam.cs.mpgc.rpg123024.application.state.GameSession;
import it.unicam.cs.mpgc.rpg123024.domain.map.Direction;
import it.unicam.cs.mpgc.rpg123024.domain.map.Dungeon;
import it.unicam.cs.mpgc.rpg123024.domain.map.Position;
import it.unicam.cs.mpgc.rpg123024.domain.model.Hero;
import it.unicam.cs.mpgc.rpg123024.domain.model.Monster;
import it.unicam.cs.mpgc.rpg123024.domain.model.Stats;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SaveGameTest {

    private static final Position MONSTER_ROOM = new Position(0, 1);

    private final EventPublisher publisher = event -> { };
    private final RecordingRepository repository = new RecordingRepository();
    private final SaveGame saveGame = new SaveGame(repository);

    private static class RecordingRepository implements GameRepository {
        private final List<GameSession> saved = new ArrayList<>();

        @Override
        public void save(GameSession session) {
            saved.add(session);
        }

        @Override
        public Optional<GameSession> load() {
            return Optional.empty();
        }
    }

    private GameSession sessionWithMonster(Hero hero, Monster monster) {
        Dungeon dungeon = Fixtures.dungeon();
        dungeon.room(MONSTER_ROOM).placeMonster(monster);
        GameSession session = Fixtures.session(hero, dungeon);
        new MoveToRoom(publisher).execute(session, Direction.EAST);
        return session;
    }

    @Test
    void savesWhileExploring() {
        GameSession session = Fixtures.session(Fixtures.hero(), Fixtures.dungeon());

        saveGame.execute(session);

        assertEquals(1, repository.saved.size());
        assertSame(session, repository.saved.get(0));
    }

    @Test
    void rejectsSavingDuringCombat() {
        GameSession session = sessionWithMonster(Fixtures.hero(), Fixtures.monster(100, 1, 1, 10));
        assertEquals("COMBAT", session.phase().id());

        assertThrows(IllegalStateException.class, () -> saveGame.execute(session));
        assertTrue(repository.saved.isEmpty());
    }

    @Test
    void rejectsSavingAfterVictory() {
        GameSession session = Fixtures.session(Fixtures.hero(), Fixtures.dungeonWithExitAt(MONSTER_ROOM));
        new MoveToRoom(publisher).execute(session, Direction.EAST);
        assertEquals("VICTORY", session.phase().id());

        assertThrows(IllegalStateException.class, () -> saveGame.execute(session));
        assertTrue(repository.saved.isEmpty());
    }

    @Test
    void rejectsSavingAfterDefeat() {
        Hero weakHero = new Hero("Hero", new Stats(5, 10, 0, 5));
        GameSession session = sessionWithMonster(weakHero, Fixtures.monster(100, 50, 1, 10));
        new ExecuteTurn(publisher).execute(session, Fixtures.ATTACK);
        assertEquals("DEFEAT", session.phase().id());

        assertThrows(IllegalStateException.class, () -> saveGame.execute(session));
        assertTrue(repository.saved.isEmpty());
    }
}

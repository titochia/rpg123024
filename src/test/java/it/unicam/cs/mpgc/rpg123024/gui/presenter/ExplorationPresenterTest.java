package it.unicam.cs.mpgc.rpg123024.gui.presenter;

import it.unicam.cs.mpgc.rpg123024.application.Fixtures;
import it.unicam.cs.mpgc.rpg123024.application.port.GameRepository;
import it.unicam.cs.mpgc.rpg123024.application.state.GameSession;
import it.unicam.cs.mpgc.rpg123024.application.usecase.MoveToRoom;
import it.unicam.cs.mpgc.rpg123024.application.usecase.SaveGame;
import it.unicam.cs.mpgc.rpg123024.domain.map.Direction;
import it.unicam.cs.mpgc.rpg123024.domain.map.Dungeon;
import it.unicam.cs.mpgc.rpg123024.domain.map.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExplorationPresenterTest {

    private final Fakes.FakeExplorationView view = new Fakes.FakeExplorationView();
    private final Fakes.FakeNavigator navigator = new Fakes.FakeNavigator();
    private final GameSessionHolder sessions = new GameSessionHolder();
    private final GameLog log = new GameLog();
    private final List<GameSession> saved = new ArrayList<>();
    private ExplorationPresenter presenter;

    @BeforeEach
    void setUp() {
        Dungeon dungeon = new Dungeon(3, 3, new Position(0, 0), new Position(2, 2));
        dungeon.room(new Position(0, 1)).placeMonster(Fixtures.monster(100, 1, 1, 10));
        dungeon.room(new Position(2, 0)).placeMonster(Fixtures.monster(100, 1, 1, 10));
        dungeon.room(new Position(1, 0)).addLoot(Fixtures.potion());
        sessions.set(Fixtures.session(Fixtures.hero(), dungeon));

        GameRepository repository = new GameRepository() {
            @Override
            public void save(GameSession session) {
                saved.add(session);
            }

            @Override
            public Optional<GameSession> load() {
                return Optional.empty();
            }
        };
        presenter = new ExplorationPresenter(view, new MoveToRoom(log::accept), new SaveGame(repository),
                sessions, log, navigator);
    }

    @Test
    void refreshShowsMapHeroAndLog() {
        presenter.refresh();

        assertEquals(3, view.rows);
        assertEquals(9, view.cells.size());
        assertTrue(view.cell(0, 0).current());
        assertTrue(view.cell(0, 0).visited());
        assertTrue(view.cell(2, 2).exit());
        assertEquals("Hero", view.hero.name());
    }

    @Test
    void refreshHidesContentOfDistantUnvisitedRooms() {
        presenter.refresh();

        assertTrue(view.cell(0, 1).monster());
        assertTrue(view.cell(1, 0).loot());
        assertFalse(view.cell(2, 0).monster());
    }

    @Test
    void moveIntoWallShowsErrorWithoutNavigating() {
        presenter.onMove(Direction.WEST);

        assertEquals(1, view.errors.size());
        assertTrue(navigator.screens.isEmpty());
    }

    @Test
    void moveIntoMonsterRoomNavigatesToCombat() {
        presenter.onMove(Direction.EAST);

        assertEquals("COMBAT", navigator.last());
    }

    @Test
    void moveToEmptyRoomStaysInExplorationAndLogsEvents() {
        presenter.onMove(Direction.SOUTH);

        assertEquals("EXPLORATION", navigator.last());
        assertFalse(log.messages().isEmpty());
    }

    @Test
    void saveDelegatesToRepositoryAndConfirms() {
        presenter.onSave();

        assertEquals(1, saved.size());
        assertEquals(List.of("Game saved"), view.messages);
    }

    @Test
    void saveDuringCombatShowsError() {
        presenter.onMove(Direction.EAST);

        presenter.onSave();

        assertTrue(saved.isEmpty());
        assertEquals(1, view.errors.size());
    }
}

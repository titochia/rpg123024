package it.unicam.cs.mpgc.rpg123024.gui.presenter;

import it.unicam.cs.mpgc.rpg123024.application.Fixtures;
import it.unicam.cs.mpgc.rpg123024.application.port.GameRepository;
import it.unicam.cs.mpgc.rpg123024.application.port.PersistenceException;
import it.unicam.cs.mpgc.rpg123024.application.state.GameSession;
import it.unicam.cs.mpgc.rpg123024.application.usecase.LoadGame;
import it.unicam.cs.mpgc.rpg123024.application.usecase.StartNewGame;
import it.unicam.cs.mpgc.rpg123024.domain.combat.AggressiveMonsterAI;
import it.unicam.cs.mpgc.rpg123024.domain.event.GameEvent;
import it.unicam.cs.mpgc.rpg123024.domain.model.Stats;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MenuPresenterTest {

    private final Fakes.FakeMenuView view = new Fakes.FakeMenuView();
    private final Fakes.FakeNavigator navigator = new Fakes.FakeNavigator();
    private final GameSessionHolder sessions = new GameSessionHolder();
    private final GameLog log = new GameLog();

    private MenuPresenter presenter(Supplier<Optional<GameSession>> saved) {
        GameRepository repository = new GameRepository() {
            @Override
            public void save(GameSession session) {
            }

            @Override
            public Optional<GameSession> load() {
                return saved.get();
            }
        };
        StartNewGame startNewGame = new StartNewGame(Fixtures::dungeon, new AggressiveMonsterAI(),
                new Stats(100, 10, 2, 5));
        return new MenuPresenter(view, startNewGame, new LoadGame(repository), sessions, log, navigator);
    }

    @Test
    void newGameStartsSessionAndShowsExploration() {
        log.accept(new GameEvent.GameOver());

        presenter(Optional::empty).onNewGame("  Aria ");

        assertEquals("Aria", sessions.require().hero().name());
        assertTrue(log.messages().isEmpty());
        assertEquals("EXPLORATION", navigator.last());
    }

    @Test
    void newGameWithBlankNameShowsError() {
        presenter(Optional::empty).onNewGame("   ");

        assertEquals(1, view.errors.size());
        assertTrue(sessions.current().isEmpty());
        assertTrue(navigator.screens.isEmpty());
    }

    @Test
    void loadWithoutSaveShowsMessage() {
        presenter(Optional::empty).onLoadGame();

        assertEquals(1, view.messages.size());
        assertTrue(sessions.current().isEmpty());
        assertTrue(navigator.screens.isEmpty());
    }

    @Test
    void loadRestoresSavedSession() {
        GameSession saved = Fixtures.session(Fixtures.hero(), Fixtures.dungeon());

        presenter(() -> Optional.of(saved)).onLoadGame();

        assertSame(saved, sessions.require());
        assertEquals("EXPLORATION", navigator.last());
    }

    @Test
    void loadFailureShowsError() {
        presenter(() -> {
            throw new PersistenceException("corrupted save");
        }).onLoadGame();

        assertEquals("corrupted save", view.errors.get(0));
        assertTrue(navigator.screens.isEmpty());
    }

    @Test
    void showOutcomeEndsCurrentGame() {
        sessions.set(Fixtures.session(Fixtures.hero(), Fixtures.dungeon()));

        presenter(Optional::empty).showOutcome(true);

        assertFalse(sessions.current().isPresent());
        assertEquals(1, view.messages.size());
    }
}

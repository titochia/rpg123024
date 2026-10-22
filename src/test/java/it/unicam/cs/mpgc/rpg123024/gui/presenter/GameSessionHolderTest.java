package it.unicam.cs.mpgc.rpg123024.gui.presenter;

import it.unicam.cs.mpgc.rpg123024.application.Fixtures;
import it.unicam.cs.mpgc.rpg123024.application.state.GameSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameSessionHolderTest {

    private final GameSessionHolder sessions = new GameSessionHolder();

    private static GameSession session() {
        return Fixtures.session(Fixtures.hero(), Fixtures.dungeon());
    }

    @Test
    void newSessionStartsWithoutUnsavedChanges() {
        sessions.set(session());

        assertFalse(sessions.hasUnsavedChanges());
    }

    @Test
    void changesAreTrackedUntilSaved() {
        sessions.set(session());

        sessions.markChanged();
        assertTrue(sessions.hasUnsavedChanges());

        sessions.markSaved();
        assertFalse(sessions.hasUnsavedChanges());
    }

    @Test
    void replacingSessionResetsUnsavedChanges() {
        sessions.set(session());
        sessions.markChanged();

        sessions.set(session());

        assertFalse(sessions.hasUnsavedChanges());
    }

    @Test
    void clearRemovesSessionAndChanges() {
        sessions.set(session());
        sessions.markChanged();

        sessions.clear();

        assertTrue(sessions.current().isEmpty());
        assertFalse(sessions.hasUnsavedChanges());
        assertThrows(IllegalStateException.class, sessions::require);
    }

    @Test
    void changesWithoutSessionAreIgnored() {
        sessions.markChanged();

        assertFalse(sessions.hasUnsavedChanges());
    }
}

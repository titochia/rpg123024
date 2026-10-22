package it.unicam.cs.mpgc.rpg123024.gui.presenter;

import it.unicam.cs.mpgc.rpg123024.application.Fixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExitPresenterTest {

    private final GameSessionHolder sessions = new GameSessionHolder();
    private final List<String> questions = new ArrayList<>();

    @BeforeEach
    void setUp() {
        sessions.set(Fixtures.session(Fixtures.hero(), Fixtures.dungeon()));
    }

    private ExitPresenter presenter(boolean answer) {
        return new ExitPresenter(message -> {
            questions.add(message);
            return answer;
        }, sessions);
    }

    @Test
    void exitsWithoutAskingWhenEverythingIsSaved() {
        assertTrue(presenter(false).onExitRequested());
        assertTrue(questions.isEmpty());
    }

    @Test
    void asksBeforeExitingWithUnsavedProgress() {
        sessions.markChanged();

        assertTrue(presenter(true).onExitRequested());
        assertEquals(1, questions.size());
    }

    @Test
    void declinedConfirmationCancelsExit() {
        sessions.markChanged();

        assertFalse(presenter(false).onExitRequested());
    }
}

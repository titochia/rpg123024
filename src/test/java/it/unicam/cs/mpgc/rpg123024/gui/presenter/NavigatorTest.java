package it.unicam.cs.mpgc.rpg123024.gui.presenter;

import it.unicam.cs.mpgc.rpg123024.application.state.GamePhase;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NavigatorTest {

    private final Fakes.FakeNavigator navigator = new Fakes.FakeNavigator();

    private static GamePhase phase(String id) {
        return () -> id;
    }

    @Test
    void showPhaseMapsEveryPhaseToItsScreen() {
        navigator.showPhase(phase("EXPLORING"));
        navigator.showPhase(phase("COMBAT"));
        navigator.showPhase(phase("VICTORY"));
        navigator.showPhase(phase("DEFEAT"));

        assertEquals(List.of("EXPLORATION", "COMBAT", "VICTORY", "DEFEAT"), navigator.screens);
    }

    @Test
    void showPhaseRejectsUnknownPhase() {
        assertThrows(IllegalStateException.class, () -> navigator.showPhase(phase("PAUSED")));
    }
}

package it.unicam.cs.mpgc.rpg123024.gui.presenter;

import it.unicam.cs.mpgc.rpg123024.domain.combat.ActionResult;
import it.unicam.cs.mpgc.rpg123024.domain.combat.CombatEngine;
import it.unicam.cs.mpgc.rpg123024.domain.event.GameEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameLogTest {

    private final GameLog log = new GameLog();

    @Test
    void formatsActionOnOpponentWithAmount() {
        log.accept(new GameEvent.ActionPerformed(new ActionResult("Aria", "Attack", "Orc", 8)));

        assertEquals(List.of("Aria uses Attack on Orc (8)"), log.messages());
    }

    @Test
    void formatsSelfActionWithoutAmount() {
        log.accept(new GameEvent.ActionPerformed(new ActionResult("Aria", "Shield", "Aria", 0)));

        assertEquals(List.of("Aria uses Shield"), log.messages());
    }

    @Test
    void formatsCombatOutcome() {
        log.accept(new GameEvent.CombatEnded(CombatEngine.Outcome.HERO_DEFEATED));

        assertEquals(List.of("You have been defeated"), log.messages());
    }

    @Test
    void keepsOnlyMostRecentMessages() {
        for (int level = 1; level <= GameLog.MAX_MESSAGES + 5; level++) {
            log.accept(new GameEvent.LevelUp(level));
        }

        assertEquals(GameLog.MAX_MESSAGES, log.messages().size());
        assertTrue(log.messages().get(0).endsWith("level 6"));
    }

    @Test
    void clearRemovesAllMessages() {
        log.accept(new GameEvent.GameWon());

        log.clear();

        assertTrue(log.messages().isEmpty());
    }
}

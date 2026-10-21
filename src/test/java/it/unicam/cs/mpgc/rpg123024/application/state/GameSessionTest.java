package it.unicam.cs.mpgc.rpg123024.application.state;

import it.unicam.cs.mpgc.rpg123024.application.Fixtures;
import it.unicam.cs.mpgc.rpg123024.domain.combat.AggressiveMonsterAI;
import it.unicam.cs.mpgc.rpg123024.domain.map.Dungeon;
import it.unicam.cs.mpgc.rpg123024.domain.map.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameSessionTest {

    @Test
    void restoredSessionStartsExploringAtGivenPosition() {
        Dungeon dungeon = Fixtures.dungeon();
        Position position = new Position(1, 0);

        GameSession session = new GameSession(Fixtures.hero(), dungeon, new AggressiveMonsterAI(), position);

        assertEquals(position, session.position());
        assertEquals("EXPLORING", session.phase().id());
        assertTrue(session.currentRoom().isVisited());
        assertFalse(dungeon.room(dungeon.start()).isVisited());
        assertTrue(session.combat().isEmpty());
    }

    @Test
    void restoredSessionRejectsPositionOutsideDungeon() {
        assertThrows(IllegalArgumentException.class, () -> new GameSession(
                Fixtures.hero(), Fixtures.dungeon(), new AggressiveMonsterAI(), new Position(5, 5)));
    }
}

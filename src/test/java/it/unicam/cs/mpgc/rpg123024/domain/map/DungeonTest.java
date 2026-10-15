package it.unicam.cs.mpgc.rpg123024.domain.map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DungeonTest {

    private Dungeon dungeon;

    @BeforeEach
    void setUp() {
        dungeon = new Dungeon(3, 4, new Position(0, 0), new Position(2, 3));
    }

    @Test
    void containsReflectsGridBounds() {
        assertTrue(dungeon.contains(new Position(2, 3)));
        assertFalse(dungeon.contains(new Position(3, 0)));
        assertFalse(dungeon.contains(new Position(0, 4)));
        assertFalse(dungeon.contains(new Position(-1, 0)));
    }

    @Test
    void roomReturnsRoomAtRequestedPosition() {
        assertEquals(new Position(1, 2), dungeon.room(new Position(1, 2)).position());
    }

    @Test
    void roomOutsideBoundsIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> dungeon.room(new Position(3, 0)));
    }

    @Test
    void startAndExitMustBeInsideGrid() {
        assertThrows(IllegalArgumentException.class,
                () -> new Dungeon(2, 2, new Position(5, 5), new Position(1, 1)));
        assertThrows(IllegalArgumentException.class,
                () -> new Dungeon(2, 2, new Position(0, 0), new Position(2, 0)));
    }

    @Test
    void moveFollowsDirectionDeltas() {
        Position origin = new Position(1, 1);

        assertEquals(new Position(0, 1), origin.move(Direction.NORTH));
        assertEquals(new Position(2, 1), origin.move(Direction.SOUTH));
        assertEquals(new Position(1, 2), origin.move(Direction.EAST));
        assertEquals(new Position(1, 0), origin.move(Direction.WEST));
    }
}
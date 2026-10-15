package it.unicam.cs.mpgc.rpg123024.domain.map;

import it.unicam.cs.mpgc.rpg123024.domain.model.Item;
import it.unicam.cs.mpgc.rpg123024.domain.model.Monster;
import it.unicam.cs.mpgc.rpg123024.domain.model.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoomTest {

    private Room room;

    @BeforeEach
    void setUp() {
        room = new Room(new Position(1, 1));
    }

    @Test
    void monsterCanBePlacedAndCleared() {
        assertTrue(room.monster().isEmpty());

        room.placeMonster(new Monster("Goblin", new Stats(10, 2, 1, 1), 5));
        assertTrue(room.monster().isPresent());

        room.clearMonster();
        assertTrue(room.monster().isEmpty());
    }

    @Test
    void collectLootReturnsItemsAndEmptiesRoom() {
        Item potion = new Item("potion", "Potion", "d", target -> { });
        Item elixir = new Item("elixir", "Elixir", "d", target -> { });
        room.addLoot(potion);
        room.addLoot(elixir);

        List<Item> collected = room.collectLoot();

        assertEquals(List.of(potion, elixir), collected);
        assertTrue(room.loot().isEmpty());
    }
}
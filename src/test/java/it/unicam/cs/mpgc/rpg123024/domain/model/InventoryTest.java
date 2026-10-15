package it.unicam.cs.mpgc.rpg123024.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InventoryTest {

    private static Item potion() {
        return new Item("potion", "Potion", "Heals 30 HP", target -> target.heal(30));
    }

    @Test
    void addFailsWhenFull() {
        Inventory inventory = new Inventory(1);

        assertTrue(inventory.add(potion()));
        assertTrue(inventory.isFull());
        assertFalse(inventory.add(potion()));
        assertEquals(1, inventory.size());
    }

    @Test
    void removeReturnsTrueOnlyIfPresent() {
        Inventory inventory = new Inventory(3);
        Item item = potion();
        inventory.add(item);

        assertTrue(inventory.remove(item));
        assertFalse(inventory.remove(item));
        assertEquals(0, inventory.size());
    }

    @Test
    void itemsViewIsUnmodifiable() {
        Inventory inventory = new Inventory(3);

        assertThrows(UnsupportedOperationException.class, () -> inventory.items().add(potion()));
    }

    @Test
    void rejectsNonPositiveCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new Inventory(0));
    }
}
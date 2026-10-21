package it.unicam.cs.mpgc.rpg123024.domain.factory;

import it.unicam.cs.mpgc.rpg123024.domain.model.Item;
import it.unicam.cs.mpgc.rpg123024.domain.random.SeededRandomSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StandardItemCatalogTest {

    private final StandardItemCatalog catalog = new StandardItemCatalog();

    @Test
    void findByIdReturnsKnownItem() {
        Item item = catalog.findById("small-potion").orElseThrow();

        assertEquals("small-potion", item.id());
    }

    @Test
    void findByIdReturnsEmptyForUnknownId() {
        assertTrue(catalog.findById("unknown").isEmpty());
    }

    @Test
    void createReturnsItemRegisteredInCatalog() {
        SeededRandomSource random = new SeededRandomSource(42);

        for (int i = 0; i < 20; i++) {
            Item created = catalog.create(random);
            assertSame(created, catalog.findById(created.id()).orElseThrow());
        }
    }
}

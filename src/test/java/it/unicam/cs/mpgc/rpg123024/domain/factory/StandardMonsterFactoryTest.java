package it.unicam.cs.mpgc.rpg123024.domain.factory;

import it.unicam.cs.mpgc.rpg123024.domain.model.Monster;
import it.unicam.cs.mpgc.rpg123024.domain.random.RandomSource;
import it.unicam.cs.mpgc.rpg123024.domain.random.SeededRandomSource;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StandardMonsterFactoryTest {

    private static final Set<String> KNOWN_NAMES = Set.of("Goblin", "Skeleton", "Orc", "Troll");

    private final StandardMonsterFactory factory = new StandardMonsterFactory();

    @Test
    void createsKnownMonstersAtFullHealth() {
        RandomSource random = new SeededRandomSource(7);

        for (int i = 0; i < 50; i++) {
            Monster monster = factory.create(random);
            assertTrue(KNOWN_NAMES.contains(monster.name()), monster.name());
            assertEquals(monster.stats().maxHealth(), monster.currentHealth());
        }
    }

    @Test
    void everyMonsterTypeCanBeCreated() {
        RandomSource random = new SeededRandomSource(7);
        Set<String> created = new HashSet<>();

        for (int i = 0; i < 200; i++) {
            created.add(factory.create(random).name());
        }

        assertEquals(KNOWN_NAMES, created);
    }

    @Test
    void firstAndLastTemplatesAreReachable() {
        assertEquals("Goblin", factory.create(bound -> 0).name());
        assertEquals("Troll", factory.create(bound -> bound - 1).name());
    }

    @Test
    void eachCallCreatesIndependentInstance() {
        RandomSource alwaysFirst = bound -> 0;
        Monster first = factory.create(alwaysFirst);
        first.takeDamage(10);

        Monster second = factory.create(alwaysFirst);

        assertNotSame(first, second);
        assertEquals(second.stats().maxHealth(), second.currentHealth());
    }
}

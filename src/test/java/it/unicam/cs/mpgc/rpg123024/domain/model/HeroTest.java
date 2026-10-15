package it.unicam.cs.mpgc.rpg123024.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HeroTest {

    private Hero hero;

    @BeforeEach
    void setUp() {
        hero = new Hero("Aria", new Stats(100, 10, 5, 5));
    }

    @Test
    void gainExperienceBelowThresholdDoesNotLevelUp() {
        int gained = hero.gainExperience(99);

        assertEquals(0, gained);
        assertEquals(1, hero.level());
        assertEquals(99, hero.experience());
    }

    @Test
    void gainExperienceLevelsUpAndGrowsStats() {
        int gained = hero.gainExperience(100);

        assertEquals(1, gained);
        assertEquals(2, hero.level());
        assertEquals(0, hero.experience());
        assertEquals(110, hero.stats().maxHealth());
        assertEquals(110, hero.currentHealth());
    }

    @Test
    void gainExperienceCanGrantMultipleLevels() {
        int gained = hero.gainExperience(300);

        assertEquals(2, gained);
        assertEquals(3, hero.level());
        assertEquals(0, hero.experience());
    }

    @Test
    void useItemConsumesItAndAppliesEffect() {
        Item potion = new Item("potion", "Potion", "Heals 30 HP", target -> target.heal(30));
        hero.inventory().add(potion);
        hero.takeDamage(50);

        boolean used = hero.useItem(potion);

        assertTrue(used);
        assertEquals(80, hero.currentHealth());
        assertEquals(0, hero.inventory().size());
    }

    @Test
    void useItemNotInInventoryHasNoEffect() {
        Item potion = new Item("potion", "Potion", "Heals 30 HP", target -> target.heal(30));
        hero.takeDamage(50);

        boolean used = hero.useItem(potion);

        assertFalse(used);
        assertEquals(50, hero.currentHealth());
    }
}
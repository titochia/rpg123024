package it.unicam.cs.mpgc.rpg123024.persistence.dto;

import it.unicam.cs.mpgc.rpg123024.application.Fixtures;
import it.unicam.cs.mpgc.rpg123024.application.port.PersistenceException;
import it.unicam.cs.mpgc.rpg123024.application.state.GameSession;
import it.unicam.cs.mpgc.rpg123024.domain.combat.AggressiveMonsterAI;
import it.unicam.cs.mpgc.rpg123024.domain.factory.StandardItemCatalog;
import it.unicam.cs.mpgc.rpg123024.domain.map.Dungeon;
import it.unicam.cs.mpgc.rpg123024.domain.map.Position;
import it.unicam.cs.mpgc.rpg123024.domain.map.Room;
import it.unicam.cs.mpgc.rpg123024.domain.model.Hero;
import it.unicam.cs.mpgc.rpg123024.domain.model.Item;
import it.unicam.cs.mpgc.rpg123024.domain.model.Monster;
import it.unicam.cs.mpgc.rpg123024.domain.model.Stats;
import it.unicam.cs.mpgc.rpg123024.persistence.dto.GameSnapshot.HeroDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnapshotMapperTest {

    private static final Position MONSTER_ROOM = new Position(1, 0);
    private static final Position LOOT_ROOM = new Position(0, 1);

    private final StandardItemCatalog catalog = new StandardItemCatalog();
    private final SnapshotMapper mapper = new SnapshotMapper(catalog, new AggressiveMonsterAI());

    private static GameSession progressedSession(StandardItemCatalog catalog) {
        Hero hero = new Hero("Aria", new Stats(100, 10, 2, 5), 5, 2, 30, 0);
        hero.heal(60);
        hero.inventory().add(catalog.findById("small-potion").orElseThrow());
        hero.inventory().add(catalog.findById("large-potion").orElseThrow());

        Dungeon dungeon = Fixtures.dungeon();
        Monster monster = new Monster("Orc", new Stats(40, 8, 1, 3), 25);
        monster.takeDamage(15);
        dungeon.room(MONSTER_ROOM).placeMonster(monster);
        dungeon.room(LOOT_ROOM).addLoot(catalog.findById("large-potion").orElseThrow());
        dungeon.room(LOOT_ROOM).markVisited();

        return new GameSession(hero, dungeon, new AggressiveMonsterAI(), LOOT_ROOM);
    }

    @Test
    void roundTripPreservesHero() {
        GameSession original = progressedSession(catalog);

        Hero restored = mapper.toSession(mapper.toSnapshot(original)).hero();

        assertEquals("Aria", restored.name());
        assertEquals(original.hero().stats(), restored.stats());
        assertEquals(2, restored.level());
        assertEquals(30, restored.experience());
        assertEquals(60, restored.currentHealth());
        assertEquals(5, restored.inventory().capacity());
        assertEquals(List.of("small-potion", "large-potion"),
                restored.inventory().items().stream().map(Item::id).toList());
    }

    @Test
    void roundTripPreservesDungeonAndPosition() {
        GameSession restored = mapper.toSession(mapper.toSnapshot(progressedSession(catalog)));
        Dungeon dungeon = restored.dungeon();

        assertEquals(LOOT_ROOM, restored.position());
        assertEquals(new Position(0, 0), dungeon.start());
        assertEquals(new Position(1, 1), dungeon.exit());
        assertEquals("EXPLORING", restored.phase().id());

        Room lootRoom = dungeon.room(LOOT_ROOM);
        assertTrue(lootRoom.isVisited());
        assertEquals(List.of("large-potion"), lootRoom.loot().stream().map(Item::id).toList());

        Room monsterRoom = dungeon.room(MONSTER_ROOM);
        assertFalse(monsterRoom.isVisited());
        Monster monster = monsterRoom.monster().orElseThrow();
        assertEquals("Orc", monster.name());
        assertEquals(25, monster.currentHealth());
        assertEquals(25, monster.experienceReward());

        assertTrue(dungeon.room(new Position(1, 1)).monster().isEmpty());
    }

    @Test
    void restoredItemsKeepTheirEffect() {
        GameSession restored = mapper.toSession(mapper.toSnapshot(progressedSession(catalog)));
        Hero hero = restored.hero();
        Item potion = hero.inventory().items().get(0);

        hero.useItem(potion);

        assertEquals(90, hero.currentHealth());
    }

    @Test
    void unknownItemIdIsRejected() {
        GameSnapshot snapshot = mapper.toSnapshot(progressedSession(catalog));
        HeroDto hero = snapshot.hero();
        HeroDto tampered = new HeroDto(hero.name(), hero.baseStats(), hero.currentHealth(), hero.level(),
                hero.experience(), hero.inventoryCapacity(), List.of("ghost-item"));
        GameSnapshot invalid = new GameSnapshot(snapshot.version(), tampered, snapshot.dungeon(), snapshot.position());

        assertThrows(PersistenceException.class, () -> mapper.toSession(invalid));
    }

    @Test
    void unsupportedVersionIsRejected() {
        GameSnapshot snapshot = mapper.toSnapshot(progressedSession(catalog));
        GameSnapshot future = new GameSnapshot(SnapshotMapper.VERSION + 1,
                snapshot.hero(), snapshot.dungeon(), snapshot.position());

        assertThrows(PersistenceException.class, () -> mapper.toSession(future));
    }
}

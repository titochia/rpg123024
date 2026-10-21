package it.unicam.cs.mpgc.rpg123024.persistence.json;

import it.unicam.cs.mpgc.rpg123024.application.Fixtures;
import it.unicam.cs.mpgc.rpg123024.application.port.PersistenceException;
import it.unicam.cs.mpgc.rpg123024.application.state.GameSession;
import it.unicam.cs.mpgc.rpg123024.domain.combat.AggressiveMonsterAI;
import it.unicam.cs.mpgc.rpg123024.domain.factory.StandardItemCatalog;
import it.unicam.cs.mpgc.rpg123024.domain.map.Dungeon;
import it.unicam.cs.mpgc.rpg123024.domain.map.Position;
import it.unicam.cs.mpgc.rpg123024.domain.model.Hero;
import it.unicam.cs.mpgc.rpg123024.domain.model.Item;
import it.unicam.cs.mpgc.rpg123024.persistence.dto.SnapshotMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonGameRepositoryTest {

    @TempDir
    Path tempDir;

    private final StandardItemCatalog catalog = new StandardItemCatalog();
    private Path file;
    private JsonGameRepository repository;

    @BeforeEach
    void setUp() {
        file = tempDir.resolve("saves").resolve("save.json");
        repository = new JsonGameRepository(file, new SnapshotMapper(catalog, new AggressiveMonsterAI()));
    }

    private GameSession session(Position position) {
        Hero hero = Fixtures.hero();
        hero.takeDamage(25);
        hero.inventory().add(catalog.findById("small-potion").orElseThrow());
        Dungeon dungeon = Fixtures.dungeon();
        dungeon.room(new Position(1, 0)).placeMonster(Fixtures.monster(30, 5, 1, 10));
        return new GameSession(hero, dungeon, new AggressiveMonsterAI(), position);
    }

    @Test
    void loadReturnsEmptyWhenFileIsMissing() {
        assertTrue(repository.load().isEmpty());
    }

    @Test
    void saveThenLoadRestoresSession() {
        repository.save(session(new Position(0, 1)));

        GameSession loaded = repository.load().orElseThrow();

        assertEquals(new Position(0, 1), loaded.position());
        assertEquals(75, loaded.hero().currentHealth());
        assertEquals(List.of("small-potion"), loaded.hero().inventory().items().stream().map(Item::id).toList());
        assertTrue(loaded.dungeon().room(new Position(1, 0)).monster().isPresent());
    }

    @Test
    void secondSaveOverwritesFirst() {
        repository.save(session(new Position(0, 1)));
        repository.save(session(new Position(1, 1)));

        assertEquals(new Position(1, 1), repository.load().orElseThrow().position());
    }

    @Test
    void saveLeavesNoTemporaryFile() throws IOException {
        repository.save(session(new Position(0, 0)));

        assertTrue(Files.exists(file));
        try (var files = Files.list(file.getParent())) {
            assertFalse(files.anyMatch(p -> p.getFileName().toString().endsWith(".tmp")));
        }
    }

    @Test
    void corruptedFileThrowsPersistenceException() throws IOException {
        Files.createDirectories(file.getParent());
        Files.writeString(file, "{ not valid json");

        assertThrows(PersistenceException.class, () -> repository.load());
    }
}

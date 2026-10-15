package it.unicam.cs.mpgc.rpg123024.domain.map;

import it.unicam.cs.mpgc.rpg123024.domain.factory.ItemFactory;
import it.unicam.cs.mpgc.rpg123024.domain.factory.MonsterFactory;
import it.unicam.cs.mpgc.rpg123024.domain.model.Item;
import it.unicam.cs.mpgc.rpg123024.domain.model.Monster;
import it.unicam.cs.mpgc.rpg123024.domain.model.Stats;
import it.unicam.cs.mpgc.rpg123024.domain.random.RandomSource;
import it.unicam.cs.mpgc.rpg123024.domain.random.SeededRandomSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomDungeonGeneratorTest {

    private static final int SIZE = 5;
    private static final MonsterFactory MONSTERS =
            random -> new Monster("M" + random.nextInt(100), new Stats(10, 2, 1, 1), 5);
    private static final ItemFactory ITEMS =
            random -> new Item("item" + random.nextInt(100), "Item", "d", target -> { });

    private static Dungeon generate(long seed, double monsterProbability, double lootProbability) {
        RandomSource random = new SeededRandomSource(seed);
        return new RandomDungeonGenerator(SIZE, SIZE, monsterProbability, lootProbability,
                random, MONSTERS, ITEMS).generate();
    }

    private static String signature(Dungeon dungeon) {
        StringBuilder builder = new StringBuilder()
                .append(dungeon.start()).append(dungeon.exit());
        for (int r = 0; r < dungeon.rows(); r++) {
            for (int c = 0; c < dungeon.cols(); c++) {
                Room room = dungeon.room(new Position(r, c));
                builder.append(room.monster().map(Monster::name).orElse("-"));
                room.loot().forEach(item -> builder.append(item.id()));
                builder.append('|');
            }
        }
        return builder.toString();
    }

    @Test
    void sameSeedProducesSameDungeon() {
        assertEquals(signature(generate(7, 0.5, 0.5)), signature(generate(7, 0.5, 0.5)));
    }

    @Test
    void startRoomIsAlwaysSafe() {
        Dungeon dungeon = generate(1, 1.0, 1.0);
        Room start = dungeon.room(dungeon.start());

        assertTrue(start.monster().isEmpty());
        assertTrue(start.loot().isEmpty());
    }

    @Test
    void exitIsFarEnoughFromStart() {
        for (long seed = 0; seed < 50; seed++) {
            Dungeon dungeon = generate(seed, 0.5, 0.5);

            assertTrue(dungeon.exit().manhattanDistance(dungeon.start()) >= SIZE);
        }
    }

    @Test
    void probabilityOneFillsEveryRoomExceptStart() {
        Dungeon dungeon = generate(3, 1.0, 1.0);

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                Position position = new Position(r, c);
                if (position.equals(dungeon.start())) {
                    continue;
                }
                assertTrue(dungeon.room(position).monster().isPresent());
                assertEquals(1, dungeon.room(position).loot().size());
            }
        }
    }

    @Test
    void probabilityZeroLeavesDungeonEmpty() {
        Dungeon dungeon = generate(3, 0.0, 0.0);

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                Room room = dungeon.room(new Position(r, c));
                assertFalse(room.monster().isPresent());
                assertTrue(room.loot().isEmpty());
            }
        }
    }

    @Test
    void rejectsInvalidArguments() {
        RandomSource random = new SeededRandomSource(1);

        assertThrows(IllegalArgumentException.class,
                () -> new RandomDungeonGenerator(1, 5, 0.5, 0.5, random, MONSTERS, ITEMS));
        assertThrows(IllegalArgumentException.class,
                () -> new RandomDungeonGenerator(5, 5, 1.5, 0.5, random, MONSTERS, ITEMS));
    }
}
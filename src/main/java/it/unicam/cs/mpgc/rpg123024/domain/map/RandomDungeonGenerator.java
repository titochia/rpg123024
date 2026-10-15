package it.unicam.cs.mpgc.rpg123024.domain.map;

import it.unicam.cs.mpgc.rpg123024.domain.factory.ItemFactory;
import it.unicam.cs.mpgc.rpg123024.domain.factory.MonsterFactory;
import it.unicam.cs.mpgc.rpg123024.domain.random.RandomSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Generatore procedurale di dungeon a griglia, deterministico a parità di {@link RandomSource}.
 * <p>
 * <b>Responsabilità:</b> posizionare partenza (angolo nord-ovest) e uscita (a distanza minima
 * dalla partenza) e popolare le stanze con mostri e bottino secondo probabilità configurabili. La
 * stanza di partenza è sempre sicura (senza mostri né bottino).
 * <p>
 * <b>Pattern:</b> Strategy concreta di {@link DungeonGenerator}. <b>SOLID:</b> Single
 * Responsibility (solo l'algoritmo di popolamento); Dependency Inversion (casualità e factory
 * iniettate); Open/Closed (contenuti variabili cambiando le factory).
 */
public class RandomDungeonGenerator implements DungeonGenerator {

    private final int rows;
    private final int cols;
    private final double monsterProbability;
    private final double lootProbability;
    private final RandomSource random;
    private final MonsterFactory monsterFactory;
    private final ItemFactory itemFactory;

    public RandomDungeonGenerator(int rows, int cols,
                                  double monsterProbability, double lootProbability,
                                  RandomSource random,
                                  MonsterFactory monsterFactory, ItemFactory itemFactory) {
        if (rows < 2 || cols < 2) {
            throw new IllegalArgumentException("rows and cols must be >= 2");
        }
        requireProbability(monsterProbability);
        requireProbability(lootProbability);
        this.rows = rows;
        this.cols = cols;
        this.monsterProbability = monsterProbability;
        this.lootProbability = lootProbability;
        this.random = Objects.requireNonNull(random, "random");
        this.monsterFactory = Objects.requireNonNull(monsterFactory, "monsterFactory");
        this.itemFactory = Objects.requireNonNull(itemFactory, "itemFactory");
    }

    @Override
    public Dungeon generate() {
        Position start = new Position(0, 0);
        Position exit = pickExit(start);
        Dungeon dungeon = new Dungeon(rows, cols, start, exit);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Position position = new Position(r, c);
                if (!position.equals(start)) {
                    populate(dungeon.room(position));
                }
            }
        }
        return dungeon;
    }

    private Position pickExit(Position start) {
        int minDistance = (rows + cols) / 2;
        List<Position> candidates = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Position candidate = new Position(r, c);
                if (candidate.manhattanDistance(start) >= minDistance) {
                    candidates.add(candidate);
                }
            }
        }
        return candidates.get(random.nextInt(candidates.size()));
    }

    private void populate(Room room) {
        if (random.chance(monsterProbability)) {
            room.placeMonster(monsterFactory.create(random));
        }
        if (random.chance(lootProbability)) {
            room.addLoot(itemFactory.create(random));
        }
    }

    private static void requireProbability(double probability) {
        if (probability < 0 || probability > 1) {
            throw new IllegalArgumentException("probability must be in [0, 1]");
        }
    }
}
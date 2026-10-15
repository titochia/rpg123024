package it.unicam.cs.mpgc.rpg123024.domain.map;

/**
 * Value object immutabile che identifica una cella (riga, colonna) della griglia del dungeon.
 * <p>
 * <b>Responsabilità:</b> rappresentare una coordinata e calcolare spostamenti e distanze, senza
 * conoscere i confini della mappa (la validità è responsabilità di {@code Dungeon}).
 * <p>
 * <b>SOLID:</b> Single Responsibility; immutabilità che elimina effetti collaterali e la rende
 * sicura come chiave o riferimento condiviso.
 *
 * @param row riga (0 = nord)
 * @param col colonna (0 = ovest)
 */
public record Position(int row, int col) {

    public Position move(Direction direction) {
        return new Position(row + direction.rowDelta(), col + direction.colDelta());
    }

    public int manhattanDistance(Position other) {
        return Math.abs(row - other.row) + Math.abs(col - other.col);
    }
}
package it.unicam.cs.mpgc.rpg123024.domain.map;

import java.util.Objects;

/**
 * Griglia rettangolare di {@link Room} con una posizione di partenza e una di uscita.
 * <p>
 * <b>Responsabilità:</b> possedere le stanze, verificare i confini ({@link #contains(Position)})
 * e fornire accesso alle stanze per posizione. Non genera il proprio contenuto (compito del
 * {@link DungeonGenerator}) né gestisce la posizione del giocatore (compito dell'Application layer).
 * <p>
 * <b>SOLID:</b> Single Responsibility (struttura e confini); la separazione dalla generazione
 * consente di cambiare algoritmo di creazione senza toccare questa classe (Open/Closed).
 * Tutte le stanze adiacenti sono collegate.
 */
public class Dungeon {

    private final Room[][] rooms;
    private final Position start;
    private final Position exit;

    public Dungeon(int rows, int cols, Position start, Position exit) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("rows and cols must be > 0");
        }
        this.rooms = new Room[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                rooms[r][c] = new Room(new Position(r, c));
            }
        }
        this.start = requireInside(start);
        this.exit = requireInside(exit);
    }

    public int rows() {
        return rooms.length;
    }

    public int cols() {
        return rooms[0].length;
    }

    public Position start() {
        return start;
    }

    public Position exit() {
        return exit;
    }

    public boolean contains(Position position) {
        return position.row() >= 0 && position.row() < rows()
                && position.col() >= 0 && position.col() < cols();
    }

    public Room room(Position position) {
        return rooms[requireInside(position).row()][position.col()];
    }

    private Position requireInside(Position position) {
        Objects.requireNonNull(position, "position");
        if (!contains(position)) {
            throw new IllegalArgumentException("position outside dungeon: " + position);
        }
        return position;
    }
}
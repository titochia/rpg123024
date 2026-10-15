package it.unicam.cs.mpgc.rpg123024.domain.map;

/**
 * Direzioni di spostamento tra stanze adiacenti della griglia.
 * <p>
 * <b>Responsabilità:</b> codificare gli spostamenti elementari come coppie (riga, colonna),
 * evitando condizionali sparsi nel codice di movimento.
 * <p>
 * <b>SOLID:</b> Single Responsibility; elimina duplicazioni (DRY) e tiene il movimento
 * indipendente dalla GUI (tasti, pulsanti o comandi web mappano tutti su questo tipo).
 */
public enum Direction {
    NORTH(-1, 0),
    SOUTH(1, 0),
    EAST(0, 1),
    WEST(0, -1);

    private final int rowDelta;
    private final int colDelta;

    Direction(int rowDelta, int colDelta) {
        this.rowDelta = rowDelta;
        this.colDelta = colDelta;
    }

    public int rowDelta() {
        return rowDelta;
    }

    public int colDelta() {
        return colDelta;
    }
}
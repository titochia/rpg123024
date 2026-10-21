package it.unicam.cs.mpgc.rpg123024.gui.view;

/**
 * View model immutabile di una cella della mappa, già filtrata dalla "nebbia di guerra".
 * <p>
 * <b>Responsabilità:</b> indicare alla View cosa disegnare per una stanza (posizione dell'eroe,
 * stanza visitata, uscita, mostro o bottino noti). Le informazioni su mostro e bottino sono
 * {@code true} solo per stanze già note al giocatore: la regola è nel Presenter, non nella View.
 * <p>
 * <b>Pattern:</b> Value Object / View Model (MVP). <b>SOLID:</b> Single Responsibility.
 */
public record MapCell(int row, int col, boolean current, boolean visited, boolean exit,
                      boolean monster, boolean loot) {
}

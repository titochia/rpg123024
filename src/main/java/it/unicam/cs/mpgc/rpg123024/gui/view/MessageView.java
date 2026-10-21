package it.unicam.cs.mpgc.rpg123024.gui.view;

/**
 * Contratto comune a tutte le View passive: mostrare un errore all'utente.
 * <p>
 * <b>Responsabilità:</b> offrire ai Presenter un unico modo per segnalare comandi rifiutati o
 * errori di persistenza, indipendentemente dalla tecnologia di presentazione.
 * <p>
 * <b>Pattern:</b> MVP (View passiva). <b>SOLID:</b> Interface Segregation (un solo metodo,
 * esteso dalle View specifiche); Liskov (ogni View è utilizzabile dove serve un {@code MessageView}).
 */
public interface MessageView {

    void showError(String message);
}

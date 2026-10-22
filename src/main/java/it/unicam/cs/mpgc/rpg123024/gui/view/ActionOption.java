package it.unicam.cs.mpgc.rpg123024.gui.view;

/**
 * View model immutabile di un'azione di combattimento: nome da mostrare e disponibilità nel turno
 * corrente (ad esempio {@code false} se l'azione è in ricarica).
 * <p>
 * <b>Responsabilità:</b> permettere alla View di disattivare le azioni non utilizzabili senza
 * conoscerne il motivo né le regole di dominio.
 * <p>
 * <b>Pattern:</b> Value Object / View Model (MVP). <b>SOLID:</b> Single Responsibility.
 */
public record ActionOption(String name, boolean available) {
}

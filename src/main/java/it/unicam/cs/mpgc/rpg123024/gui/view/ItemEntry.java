package it.unicam.cs.mpgc.rpg123024.gui.view;

/**
 * View model immutabile di un oggetto dell'inventario.
 * <p>
 * <b>Responsabilità:</b> fornire alla View nome e descrizione da mostrare; l'oggetto di dominio
 * resta nel Presenter, che lo individua per indice quando l'utente lo seleziona.
 * <p>
 * <b>Pattern:</b> Value Object / View Model (MVP). <b>SOLID:</b> Single Responsibility.
 */
public record ItemEntry(String name, String description) {
}

package it.unicam.cs.mpgc.rpg123024.gui.view;

/**
 * Contratto per chiedere all'utente una conferma sì/no.
 * <p>
 * <b>Responsabilità:</b> permettere ai Presenter di chiedere conferma prima di azioni che farebbero
 * perdere progressi, senza conoscere la tecnologia del dialogo (finestra modale, pagina web, ecc.).
 * <p>
 * <b>Pattern:</b> MVP (View passiva). <b>SOLID:</b> Interface Segregation (un solo metodo,
 * combinabile con le altre View); Dependency Inversion; nei test basta una lambda.
 */
@FunctionalInterface
public interface ConfirmView {

    boolean confirm(String message);
}

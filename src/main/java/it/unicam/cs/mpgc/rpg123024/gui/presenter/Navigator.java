package it.unicam.cs.mpgc.rpg123024.gui.presenter;

import it.unicam.cs.mpgc.rpg123024.application.state.GamePhase;

/**
 * Astrazione della navigazione tra schermate.
 * <p>
 * <b>Responsabilità:</b> permettere ai Presenter di chiedere il cambio di schermata senza conoscere
 * finestre, scene o route; {@link #showPhase(GamePhase)} traduce la fase corrente della partita
 * nella schermata corrispondente, in un solo punto.
 * <p>
 * <b>SOLID:</b> Dependency Inversion (i Presenter dipendono da questa interfaccia, l'implementazione
 * JavaFX è fornita dalla composition root); Single Responsibility (solo navigazione); i Presenter
 * restano testabili con un navigatore fittizio.
 */
public interface Navigator {

    void showMenu();

    void showExploration();

    void showCombat();

    void showInventory();

    void showGameEnd(boolean victory);

    default void showPhase(GamePhase phase) {
        switch (phase.id()) {
            case "EXPLORING" -> showExploration();
            case "COMBAT" -> showCombat();
            case "VICTORY" -> showGameEnd(true);
            case "DEFEAT" -> showGameEnd(false);
            default -> throw new IllegalStateException("unknown phase: " + phase.id());
        }
    }
}

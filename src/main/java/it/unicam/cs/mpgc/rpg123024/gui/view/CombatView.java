package it.unicam.cs.mpgc.rpg123024.gui.view;

import java.util.List;

/**
 * View passiva del combattimento: stato dei due contendenti, azioni disponibili e registro.
 * <p>
 * <b>Responsabilità:</b> mostrare i dati ricevuti e comunicare al Presenter l'indice dell'azione
 * scelta o la richiesta di aprire l'inventario. Non conosce le azioni di dominio, solo i loro nomi e
 * la loro disponibilità ({@link ActionOption}).
 * <p>
 * <b>Pattern:</b> MVP (View passiva). <b>SOLID:</b> Dependency Inversion; Open/Closed (nuove azioni
 * compaiono come nuovi nomi, senza modificare la View).
 */
public interface CombatView extends MessageView {

    void showCombatants(CombatantStatus hero, CombatantStatus monster);

    void showActions(List<ActionOption> actions);

    void showLog(List<String> messages);
}

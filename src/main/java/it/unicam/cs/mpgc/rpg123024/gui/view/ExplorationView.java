package it.unicam.cs.mpgc.rpg123024.gui.view;

import java.util.List;

/**
 * View passiva dell'esplorazione: mappa del dungeon, stato dell'eroe e registro degli eventi.
 * <p>
 * <b>Responsabilità:</b> disegnare i dati ricevuti e inoltrare al Presenter i comandi dell'utente
 * (spostamento, salvataggio, inventario, ritorno al menu). Non conosce il Domain.
 * <p>
 * <b>Pattern:</b> MVP (View passiva). <b>SOLID:</b> Dependency Inversion; Interface Segregation
 * (solo i metodi necessari a questa schermata).
 */
public interface ExplorationView extends MessageView {

    void showMap(int rows, int cols, List<MapCell> cells);

    void showHero(HeroStatus hero);

    void showLog(List<String> messages);

    void showMessage(String message);
}

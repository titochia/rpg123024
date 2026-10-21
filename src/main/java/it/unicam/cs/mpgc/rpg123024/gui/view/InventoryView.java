package it.unicam.cs.mpgc.rpg123024.gui.view;

import java.util.List;

/**
 * View passiva dell'inventario.
 * <p>
 * <b>Responsabilità:</b> elencare gli oggetti posseduti e lo stato dell'eroe, e inoltrare al
 * Presenter l'indice dell'oggetto da usare o la richiesta di tornare indietro.
 * <p>
 * <b>Pattern:</b> MVP (View passiva). <b>SOLID:</b> Dependency Inversion; Interface Segregation.
 */
public interface InventoryView extends MessageView {

    void showItems(List<ItemEntry> items, int capacity);

    void showHero(HeroStatus hero);
}

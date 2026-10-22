package it.unicam.cs.mpgc.rpg123024.gui.view;

/**
 * View passiva del menu principale (nuova partita, continua, caricamento, esito della partita
 * precedente).
 * <p>
 * <b>Responsabilità:</b> visualizzare messaggi informativi e d'errore, mostrare o nascondere il
 * comando "continua", chiedere conferme e inoltrare al Presenter le scelte dell'utente. Non contiene
 * logica.
 * <p>
 * <b>Pattern:</b> MVP (View passiva). <b>SOLID:</b> Dependency Inversion (il Presenter dipende da
 * questa interfaccia, non da JavaFX); Open/Closed (nuove tecnologie di UI = nuove implementazioni);
 * Interface Segregation (compone {@link MessageView} e {@link ConfirmView}).
 */
public interface MenuView extends MessageView, ConfirmView {

    void showMessage(String message);

    void setContinueAvailable(boolean available);
}

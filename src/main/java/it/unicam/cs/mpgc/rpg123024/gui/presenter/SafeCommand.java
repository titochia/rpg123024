package it.unicam.cs.mpgc.rpg123024.gui.presenter;

import it.unicam.cs.mpgc.rpg123024.application.port.PersistenceException;
import it.unicam.cs.mpgc.rpg123024.gui.view.MessageView;

/**
 * Esecuzione protetta dei comandi dell'utente.
 * <p>
 * <b>Responsabilità:</b> eseguire un comando e, se il core lo rifiuta (comando non ammesso nella
 * fase, argomento non valido, errore di persistenza), mostrarne il messaggio nella View invece di
 * propagare l'eccezione all'interfaccia grafica.
 * <p>
 * <b>SOLID:</b> Single Responsibility (solo gestione uniforme degli errori); DRY (stessa politica
 * per tutti i Presenter). Gli errori di programmazione non previsti non vengono intercettati.
 */
final class SafeCommand {

    private SafeCommand() {
    }

    static boolean run(MessageView view, Runnable command) {
        try {
            command.run();
            return true;
        } catch (IllegalStateException | IllegalArgumentException | PersistenceException e) {
            view.showError(e.getMessage());
            return false;
        }
    }
}

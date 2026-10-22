package it.unicam.cs.mpgc.rpg123024.gui.presenter;

import it.unicam.cs.mpgc.rpg123024.gui.view.ConfirmView;

import java.util.Objects;

/**
 * Presenter della richiesta di chiusura dell'applicazione.
 * <p>
 * <b>Responsabilità:</b> decidere se l'applicazione può chiudersi: senza progressi non salvati sì,
 * altrimenti solo dopo la conferma dell'utente. Non chiude nulla: restituisce la decisione a chi
 * gestisce la finestra.
 * <p>
 * <b>Pattern:</b> MVP (Presenter). <b>SOLID:</b> Single Responsibility (solo la regola di uscita,
 * valida da qualsiasi schermata); Dependency Inversion (dipende da {@link ConfirmView}, non da
 * JavaFX).
 */
public class ExitPresenter {

    private final ConfirmView view;
    private final GameSessionHolder sessions;

    public ExitPresenter(ConfirmView view, GameSessionHolder sessions) {
        this.view = Objects.requireNonNull(view, "view");
        this.sessions = Objects.requireNonNull(sessions, "sessions");
    }

    public boolean onExitRequested() {
        return !sessions.hasUnsavedChanges()
                || view.confirm("The current game has unsaved progress. Quit anyway?");
    }
}

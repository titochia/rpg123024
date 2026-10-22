package it.unicam.cs.mpgc.rpg123024.gui.presenter;

import it.unicam.cs.mpgc.rpg123024.application.state.GameSession;

import java.util.Objects;
import java.util.Optional;

/**
 * Contenitore della partita corrente condiviso tra i Presenter.
 * <p>
 * <b>Responsabilità:</b> mantenere il riferimento alla {@link GameSession} attiva (impostata da
 * nuova partita o caricamento), fornirla ai Presenter delle varie schermate e ricordare se ci sono
 * progressi non ancora salvati, così da chiedere conferma prima di perderli.
 * <p>
 * <b>SOLID:</b> Single Responsibility (custodia della partita corrente e del suo stato di
 * salvataggio); evita che i Presenter si passino la sessione tra loro o che la conosca il navigatore
 * (basso accoppiamento).
 */
public class GameSessionHolder {

    private GameSession session;
    private boolean unsavedChanges;

    public void set(GameSession session) {
        this.session = Objects.requireNonNull(session, "session");
        this.unsavedChanges = false;
    }

    public Optional<GameSession> current() {
        return Optional.ofNullable(session);
    }

    public GameSession require() {
        return current().orElseThrow(() -> new IllegalStateException("no game in progress"));
    }

    public void clear() {
        session = null;
        unsavedChanges = false;
    }

    public void markChanged() {
        unsavedChanges = session != null;
    }

    public void markSaved() {
        unsavedChanges = false;
    }

    public boolean hasUnsavedChanges() {
        return unsavedChanges;
    }
}

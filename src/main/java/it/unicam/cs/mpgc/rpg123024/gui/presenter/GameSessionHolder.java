package it.unicam.cs.mpgc.rpg123024.gui.presenter;

import it.unicam.cs.mpgc.rpg123024.application.state.GameSession;

import java.util.Objects;
import java.util.Optional;

/**
 * Contenitore della partita corrente condiviso tra i Presenter.
 * <p>
 * <b>Responsabilità:</b> mantenere il riferimento alla {@link GameSession} attiva (impostata da
 * nuova partita o caricamento) e fornirla ai Presenter delle varie schermate.
 * <p>
 * <b>SOLID:</b> Single Responsibility (solo custodia del riferimento); evita che i Presenter si
 * passino la sessione tra loro o che la conosca il navigatore (basso accoppiamento).
 */
public class GameSessionHolder {

    private GameSession session;

    public void set(GameSession session) {
        this.session = Objects.requireNonNull(session, "session");
    }

    public Optional<GameSession> current() {
        return Optional.ofNullable(session);
    }

    public GameSession require() {
        return current().orElseThrow(() -> new IllegalStateException("no game in progress"));
    }

    public void clear() {
        session = null;
    }
}

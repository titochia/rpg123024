package it.unicam.cs.mpgc.rpg123024.gui.presenter;

import it.unicam.cs.mpgc.rpg123024.application.state.GameSession;
import it.unicam.cs.mpgc.rpg123024.application.usecase.LoadGame;
import it.unicam.cs.mpgc.rpg123024.application.usecase.StartNewGame;
import it.unicam.cs.mpgc.rpg123024.gui.view.MenuView;

import java.util.Objects;
import java.util.Optional;

/**
 * Presenter del menu principale.
 * <p>
 * <b>Responsabilità:</b> avviare una nuova partita o caricare quella salvata tramite gli use case,
 * registrarla nel {@link GameSessionHolder}, azzerare il registro e navigare alla schermata della
 * fase corrente; mostrare l'esito della partita conclusa.
 * <p>
 * <b>Pattern:</b> MVP (Presenter). <b>SOLID:</b> Single Responsibility (logica del menu);
 * Dependency Inversion (dipende da {@link MenuView}, {@link Navigator} e use case, non da JavaFX):
 * testabile senza interfaccia grafica.
 */
public class MenuPresenter {

    private final MenuView view;
    private final StartNewGame startNewGame;
    private final LoadGame loadGame;
    private final GameSessionHolder sessions;
    private final GameLog log;
    private final Navigator navigator;

    public MenuPresenter(MenuView view, StartNewGame startNewGame, LoadGame loadGame,
                         GameSessionHolder sessions, GameLog log, Navigator navigator) {
        this.view = Objects.requireNonNull(view, "view");
        this.startNewGame = Objects.requireNonNull(startNewGame, "startNewGame");
        this.loadGame = Objects.requireNonNull(loadGame, "loadGame");
        this.sessions = Objects.requireNonNull(sessions, "sessions");
        this.log = Objects.requireNonNull(log, "log");
        this.navigator = Objects.requireNonNull(navigator, "navigator");
    }

    public void onNewGame(String heroName) {
        if (heroName == null || heroName.isBlank()) {
            view.showError("Enter a name for your hero");
            return;
        }
        SafeCommand.run(view, () -> begin(startNewGame.execute(heroName.strip())));
    }

    public void onLoadGame() {
        SafeCommand.run(view, () -> {
            Optional<GameSession> loaded = loadGame.execute();
            if (loaded.isEmpty()) {
                view.showMessage("No saved game found");
                return;
            }
            begin(loaded.get());
        });
    }

    public void showOutcome(boolean victory) {
        sessions.clear();
        view.showMessage(victory ? "Victory! You escaped the dungeon." : "Defeat. Your hero has fallen.");
    }

    private void begin(GameSession session) {
        sessions.set(session);
        log.clear();
        navigator.showPhase(session.phase());
    }
}

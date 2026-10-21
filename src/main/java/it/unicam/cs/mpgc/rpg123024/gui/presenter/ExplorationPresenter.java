package it.unicam.cs.mpgc.rpg123024.gui.presenter;

import it.unicam.cs.mpgc.rpg123024.application.state.GameSession;
import it.unicam.cs.mpgc.rpg123024.application.usecase.MoveToRoom;
import it.unicam.cs.mpgc.rpg123024.application.usecase.SaveGame;
import it.unicam.cs.mpgc.rpg123024.domain.map.Direction;
import it.unicam.cs.mpgc.rpg123024.domain.map.Dungeon;
import it.unicam.cs.mpgc.rpg123024.domain.map.Position;
import it.unicam.cs.mpgc.rpg123024.domain.map.Room;
import it.unicam.cs.mpgc.rpg123024.gui.view.ExplorationView;
import it.unicam.cs.mpgc.rpg123024.gui.view.MapCell;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Presenter della schermata di esplorazione.
 * <p>
 * <b>Responsabilità:</b> preparare mappa (con "nebbia di guerra": mostri e bottino visibili solo
 * nelle stanze visitate o adiacenti all'eroe), stato dell'eroe e registro; tradurre i comandi
 * dell'utente in use case (spostamento, salvataggio) e navigare alla schermata della fase risultante.
 * <p>
 * <b>Pattern:</b> MVP (Presenter). <b>SOLID:</b> Single Responsibility (logica di presentazione
 * dell'esplorazione; le regole di gioco restano nelle fasi); Dependency Inversion (View, Navigator e
 * use case astratti o iniettati).
 */
public class ExplorationPresenter {

    private final ExplorationView view;
    private final MoveToRoom moveToRoom;
    private final SaveGame saveGame;
    private final GameSessionHolder sessions;
    private final GameLog log;
    private final Navigator navigator;

    public ExplorationPresenter(ExplorationView view, MoveToRoom moveToRoom, SaveGame saveGame,
                                GameSessionHolder sessions, GameLog log, Navigator navigator) {
        this.view = Objects.requireNonNull(view, "view");
        this.moveToRoom = Objects.requireNonNull(moveToRoom, "moveToRoom");
        this.saveGame = Objects.requireNonNull(saveGame, "saveGame");
        this.sessions = Objects.requireNonNull(sessions, "sessions");
        this.log = Objects.requireNonNull(log, "log");
        this.navigator = Objects.requireNonNull(navigator, "navigator");
    }

    public void refresh() {
        GameSession session = sessions.require();
        Dungeon dungeon = session.dungeon();
        view.showMap(dungeon.rows(), dungeon.cols(), cells(session));
        view.showHero(ViewModels.heroStatus(session.hero()));
        view.showLog(log.messages());
    }

    public void onMove(Direction direction) {
        GameSession session = sessions.require();
        if (SafeCommand.run(view, () -> moveToRoom.execute(session, direction))) {
            navigator.showPhase(session.phase());
        }
    }

    public void onSave() {
        GameSession session = sessions.require();
        if (SafeCommand.run(view, () -> saveGame.execute(session))) {
            view.showMessage("Game saved");
        }
    }

    public void onOpenInventory() {
        navigator.showInventory();
    }

    public void onBackToMenu() {
        navigator.showMenu();
    }

    private static List<MapCell> cells(GameSession session) {
        Dungeon dungeon = session.dungeon();
        Position here = session.position();
        List<MapCell> cells = new ArrayList<>();
        for (int r = 0; r < dungeon.rows(); r++) {
            for (int c = 0; c < dungeon.cols(); c++) {
                Position position = new Position(r, c);
                Room room = dungeon.room(position);
                boolean known = room.isVisited() || position.manhattanDistance(here) <= 1;
                cells.add(new MapCell(r, c, position.equals(here), room.isVisited(),
                        position.equals(dungeon.exit()),
                        known && room.monster().isPresent(),
                        known && !room.loot().isEmpty()));
            }
        }
        return cells;
    }
}

package it.unicam.cs.mpgc.rpg123024.gui.javafx;

import it.unicam.cs.mpgc.rpg123024.domain.map.Direction;
import it.unicam.cs.mpgc.rpg123024.gui.presenter.ExplorationPresenter;
import it.unicam.cs.mpgc.rpg123024.gui.view.ExplorationView;
import it.unicam.cs.mpgc.rpg123024.gui.view.HeroStatus;
import it.unicam.cs.mpgc.rpg123024.gui.view.MapCell;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Objects;

/**
 * Implementazione JavaFX della {@link ExplorationView}: griglia delle stanze, pannello dell'eroe,
 * registro degli eventi e comandi (frecce/WASD o pulsanti direzionali, inventario, salvataggio, menu).
 * <p>
 * <b>Responsabilità:</b> disegnare le {@link MapCell} ricevute (simboli e stili, senza conoscere le
 * regole di visibilità) e tradurre tasti e clic in chiamate all'{@link ExplorationPresenter}.
 * <p>
 * <b>Pattern:</b> MVP (View passiva). <b>SOLID:</b> Single Responsibility (rendering e input);
 * Liskov (sostituibile con qualsiasi altra {@code ExplorationView}).
 */
public class FxExplorationView implements ExplorationView {

    private static final double TILE_SIZE = 64;

    private final BorderPane root = new BorderPane();
    private final GridPane map = new GridPane();
    private final Label heroName = new Label();
    private final Label progress = new Label();
    private final ProgressBar health = Widgets.healthBar();
    private final Label healthText = new Label();
    private final Label stats = new Label();
    private final ListView<String> log = Widgets.logList();
    private final Label status = Widgets.statusLabel();
    private ExplorationPresenter presenter;

    public FxExplorationView() {
        map.getStyleClass().add("map");
        map.setHgap(4);
        map.setVgap(4);
        map.setAlignment(Pos.CENTER);

        heroName.getStyleClass().add("heading");
        Label logTitle = new Label("Log");
        logTitle.getStyleClass().add("heading");
        VBox side = new VBox(8, heroName, progress, health, healthText, stats, logTitle, log);
        side.getStyleClass().add("panel");
        side.setPrefWidth(300);
        VBox.setVgrow(log, Priority.ALWAYS);

        HBox commands = new HBox(10,
                Widgets.button("Inventory", () -> presenter.onOpenInventory()),
                Widgets.button("Save", () -> presenter.onSave()),
                Widgets.button("Menu", () -> presenter.onBackToMenu()));
        Label hint = new Label("Move with arrow keys or WASD");
        hint.getStyleClass().add("subtitle");
        HBox bottom = new HBox(30, directionPad(), new VBox(10, commands, hint));
        bottom.setAlignment(Pos.CENTER_LEFT);
        bottom.setPadding(new Insets(12, 0, 0, 0));

        root.setTop(status);
        root.setCenter(map);
        root.setRight(side);
        root.setBottom(bottom);
        root.getStyleClass().add("screen");
        root.addEventFilter(KeyEvent.KEY_PRESSED, this::onKeyPressed);
    }

    public void setPresenter(ExplorationPresenter presenter) {
        this.presenter = Objects.requireNonNull(presenter, "presenter");
    }

    public Parent root() {
        return root;
    }

    @Override
    public void showMap(int rows, int cols, List<MapCell> cells) {
        Widgets.clearStatus(status);
        map.getChildren().clear();
        for (MapCell cell : cells) {
            map.add(tile(cell), cell.col(), cell.row());
        }
    }

    @Override
    public void showHero(HeroStatus hero) {
        heroName.setText(hero.name());
        progress.setText("Level " + hero.level() + "  -  XP " + hero.experience() + " / " + hero.experienceToNextLevel());
        Widgets.updateHealth(health, healthText, hero.health(), hero.maxHealth());
        stats.setText("ATK " + hero.attack() + "   DEF " + hero.defense() + "   SPD " + hero.speed());
    }

    @Override
    public void showLog(List<String> messages) {
        Widgets.showLog(log, messages);
    }

    @Override
    public void showMessage(String message) {
        Widgets.setStatus(status, message, false);
    }

    @Override
    public void showError(String message) {
        Widgets.setStatus(status, message, true);
    }

    private StackPane tile(MapCell cell) {
        Label symbol = new Label(symbol(cell));
        symbol.getStyleClass().add("tile-symbol");
        StackPane tile = new StackPane(symbol);
        tile.setPrefSize(TILE_SIZE, TILE_SIZE);
        tile.getStyleClass().addAll("tile", cell.visited() ? "tile-visited" : "tile-unknown");
        if (cell.exit()) {
            tile.getStyleClass().add("tile-exit");
        }
        if (cell.current()) {
            tile.getStyleClass().add("tile-current");
        } else if (cell.monster()) {
            tile.getStyleClass().add("tile-monster");
        }
        return tile;
    }

    private static String symbol(MapCell cell) {
        if (cell.current()) {
            return "@";
        }
        if (cell.monster()) {
            return "M";
        }
        if (cell.loot()) {
            return "*";
        }
        return cell.exit() ? "EXIT" : "";
    }

    private GridPane directionPad() {
        GridPane pad = new GridPane();
        pad.setHgap(4);
        pad.setVgap(4);
        pad.add(Widgets.button("N", () -> presenter.onMove(Direction.NORTH)), 1, 0);
        pad.add(Widgets.button("W", () -> presenter.onMove(Direction.WEST)), 0, 1);
        pad.add(Widgets.button("E", () -> presenter.onMove(Direction.EAST)), 2, 1);
        pad.add(Widgets.button("S", () -> presenter.onMove(Direction.SOUTH)), 1, 2);
        return pad;
    }

    private void onKeyPressed(KeyEvent event) {
        Direction direction = switch (event.getCode()) {
            case UP, W -> Direction.NORTH;
            case DOWN, S -> Direction.SOUTH;
            case LEFT, A -> Direction.WEST;
            case RIGHT, D -> Direction.EAST;
            default -> null;
        };
        if (direction != null) {
            event.consume();
            presenter.onMove(direction);
        }
    }
}

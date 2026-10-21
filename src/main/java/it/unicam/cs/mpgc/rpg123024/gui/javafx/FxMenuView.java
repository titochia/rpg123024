package it.unicam.cs.mpgc.rpg123024.gui.javafx;

import it.unicam.cs.mpgc.rpg123024.gui.presenter.MenuPresenter;
import it.unicam.cs.mpgc.rpg123024.gui.view.MenuView;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.Objects;

/**
 * Implementazione JavaFX della {@link MenuView}: titolo, nome dell'eroe, nuova partita,
 * caricamento e uscita.
 * <p>
 * <b>Responsabilità:</b> costruire la schermata e inoltrare le scelte al {@link MenuPresenter};
 * mostrare messaggi ed errori ricevuti. Nessuna logica di gioco.
 * <p>
 * <b>Pattern:</b> MVP (View passiva). <b>SOLID:</b> Single Responsibility (solo rendering e input);
 * Liskov (sostituibile con qualsiasi altra {@code MenuView}).
 */
public class FxMenuView implements MenuView {

    private static final double CONTROL_WIDTH = 260;

    private final VBox root = new VBox(14);
    private final TextField heroName = new TextField();
    private final Label status = Widgets.statusLabel();
    private MenuPresenter presenter;

    public FxMenuView() {
        Label title = new Label("Dungeon Crawler");
        title.getStyleClass().add("title");
        Label subtitle = new Label("Find the exit. Survive the monsters.");
        subtitle.getStyleClass().add("subtitle");

        heroName.setPromptText("Hero name");
        heroName.setMaxWidth(CONTROL_WIDTH);
        heroName.setOnAction(event -> presenter.onNewGame(heroName.getText()));

        Button newGame = Widgets.button("New game", () -> presenter.onNewGame(heroName.getText()));
        Button load = Widgets.button("Load game", () -> presenter.onLoadGame());
        Button quit = Widgets.button("Quit", Platform::exit);
        for (Button button : new Button[] {newGame, load, quit}) {
            button.setPrefWidth(CONTROL_WIDTH);
        }
        status.setMaxWidth(CONTROL_WIDTH * 1.5);

        root.getChildren().addAll(title, subtitle, heroName, newGame, load, quit, status);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("screen");
    }

    public void setPresenter(MenuPresenter presenter) {
        this.presenter = Objects.requireNonNull(presenter, "presenter");
    }

    public Parent root() {
        return root;
    }

    public void clearStatus() {
        Widgets.clearStatus(status);
    }

    @Override
    public void showMessage(String message) {
        Widgets.setStatus(status, message, false);
    }

    @Override
    public void showError(String message) {
        Widgets.setStatus(status, message, true);
    }
}

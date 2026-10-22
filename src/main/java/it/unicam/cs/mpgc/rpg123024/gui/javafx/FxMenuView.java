package it.unicam.cs.mpgc.rpg123024.gui.javafx;

import it.unicam.cs.mpgc.rpg123024.gui.presenter.MenuPresenter;
import it.unicam.cs.mpgc.rpg123024.gui.view.ConfirmView;
import it.unicam.cs.mpgc.rpg123024.gui.view.MenuView;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.util.Objects;

/**
 * Implementazione JavaFX della {@link MenuView}: titolo, nome dell'eroe, nuova partita, continua,
 * caricamento e uscita.
 * <p>
 * <b>Responsabilità:</b> costruire la schermata e inoltrare le scelte al {@link MenuPresenter};
 * mostrare messaggi, errori e conferme. Il pulsante di uscita genera una normale richiesta di
 * chiusura della finestra, così passa dagli stessi controlli della chiusura con la "X". Nessuna
 * logica di gioco.
 * <p>
 * <b>Pattern:</b> MVP (View passiva). <b>SOLID:</b> Single Responsibility (solo rendering e input);
 * Liskov (sostituibile con qualsiasi altra {@code MenuView}); la conferma è delegata a una
 * {@link ConfirmView} (composizione).
 */
public class FxMenuView implements MenuView {

    private static final double CONTROL_WIDTH = 260;

    private final VBox root = new VBox(14);
    private final TextField heroName = new TextField();
    private final Button continueGame;
    private final Label status = Widgets.statusLabel();
    private final ConfirmView dialog = new FxConfirmDialog();
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
        continueGame = Widgets.button("Continue", () -> presenter.onContinue());
        Button load = Widgets.button("Load game", () -> presenter.onLoadGame());
        Button quit = Widgets.button("Quit", this::requestClose);
        for (Button button : new Button[] {newGame, continueGame, load, quit}) {
            button.setPrefWidth(CONTROL_WIDTH);
        }
        continueGame.managedProperty().bind(continueGame.visibleProperty());
        continueGame.setVisible(false);
        status.setMaxWidth(CONTROL_WIDTH * 1.5);

        root.getChildren().addAll(title, subtitle, heroName, newGame, continueGame, load, quit, status);
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

    @Override
    public void setContinueAvailable(boolean available) {
        continueGame.setVisible(available);
    }

    @Override
    public boolean confirm(String message) {
        return dialog.confirm(message);
    }

    private void requestClose() {
        Window window = root.getScene().getWindow();
        window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
    }
}

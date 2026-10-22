package it.unicam.cs.mpgc.rpg123024.gui.javafx;

import it.unicam.cs.mpgc.rpg123024.gui.presenter.CombatPresenter;
import it.unicam.cs.mpgc.rpg123024.gui.view.ActionOption;
import it.unicam.cs.mpgc.rpg123024.gui.view.CombatView;
import it.unicam.cs.mpgc.rpg123024.gui.view.CombatantStatus;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Objects;

/**
 * Implementazione JavaFX della {@link CombatView}: pannelli dei due contendenti (salute, statistiche
 * effettive, effetti attivi), pulsanti delle azioni e registro dello scontro.
 * <p>
 * <b>Responsabilità:</b> mostrare i dati ricevuti e creare un pulsante per ogni azione (disattivato
 * se l'azione non è disponibile, ad esempio in ricarica), comunicandone l'indice al
 * {@link CombatPresenter}. Non conosce le azioni di dominio.
 * <p>
 * <b>Pattern:</b> MVP (View passiva). <b>SOLID:</b> Single Responsibility; Open/Closed (i pulsanti
 * sono generati dall'elenco ricevuto, quindi nuove azioni non richiedono modifiche).
 */
public class FxCombatView implements CombatView {

    private final BorderPane root = new BorderPane();
    private final FighterPanel heroPanel = new FighterPanel();
    private final FighterPanel monsterPanel = new FighterPanel();
    private final FlowPane actions = new FlowPane(10, 10);
    private final ListView<String> log = Widgets.logList();
    private final Label status = Widgets.statusLabel();
    private CombatPresenter presenter;

    public FxCombatView() {
        Label versus = new Label("VS");
        versus.getStyleClass().add("title");
        HBox fighters = new HBox(40, heroPanel.root, versus, monsterPanel.root);
        fighters.setAlignment(Pos.CENTER);
        HBox.setHgrow(heroPanel.root, Priority.ALWAYS);
        HBox.setHgrow(monsterPanel.root, Priority.ALWAYS);
        monsterPanel.root.getStyleClass().add("enemy");

        actions.setAlignment(Pos.CENTER);
        log.setPrefHeight(180);
        VBox center = new VBox(16, fighters, actions, log);

        root.setTop(status);
        root.setCenter(center);
        root.getStyleClass().add("screen");
    }

    public void setPresenter(CombatPresenter presenter) {
        this.presenter = Objects.requireNonNull(presenter, "presenter");
    }

    public Parent root() {
        return root;
    }

    @Override
    public void showCombatants(CombatantStatus hero, CombatantStatus monster) {
        Widgets.clearStatus(status);
        heroPanel.update(hero);
        monsterPanel.update(monster);
    }

    @Override
    public void showActions(List<ActionOption> options) {
        actions.getChildren().clear();
        for (int i = 0; i < options.size(); i++) {
            int index = i;
            Button button = Widgets.button(options.get(i).name(), () -> presenter.onAction(index));
            button.getStyleClass().add("action");
            button.setDisable(!options.get(i).available());
            actions.getChildren().add(button);
        }
        actions.getChildren().add(Widgets.button("Inventory", () -> presenter.onOpenInventory()));
    }

    @Override
    public void showLog(List<String> messages) {
        Widgets.showLog(log, messages);
    }

    @Override
    public void showError(String message) {
        Widgets.setStatus(status, message, true);
    }

    private static final class FighterPanel {
        private final Label name = new Label();
        private final ProgressBar health = Widgets.healthBar();
        private final Label healthText = new Label();
        private final Label stats = new Label();
        private final Label effects = new Label();
        private final VBox root = new VBox(8, name, health, healthText, stats, effects);

        private FighterPanel() {
            name.getStyleClass().add("heading");
            effects.getStyleClass().add("effects");
            root.getStyleClass().add("panel");
            root.setPrefWidth(320);
        }

        private void update(CombatantStatus status) {
            name.setText(status.name());
            Widgets.updateHealth(health, healthText, status.health(), status.maxHealth());
            stats.setText("ATK " + status.attack() + "   DEF " + status.defense() + "   SPD " + status.speed());
            effects.setText(status.effects().isEmpty() ? "No active effects" : String.join(", ", status.effects()));
        }
    }
}

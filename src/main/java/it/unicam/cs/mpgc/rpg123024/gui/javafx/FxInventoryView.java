package it.unicam.cs.mpgc.rpg123024.gui.javafx;

import it.unicam.cs.mpgc.rpg123024.gui.presenter.InventoryPresenter;
import it.unicam.cs.mpgc.rpg123024.gui.view.HeroStatus;
import it.unicam.cs.mpgc.rpg123024.gui.view.InventoryView;
import it.unicam.cs.mpgc.rpg123024.gui.view.ItemEntry;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Objects;

/**
 * Implementazione JavaFX della {@link InventoryView}: elenco degli oggetti, capienza, salute
 * dell'eroe e comandi "usa" (anche con doppio clic) e "indietro".
 * <p>
 * <b>Responsabilità:</b> mostrare gli {@link ItemEntry} ricevuti e comunicare l'indice
 * dell'oggetto selezionato all'{@link InventoryPresenter}.
 * <p>
 * <b>Pattern:</b> MVP (View passiva). <b>SOLID:</b> Single Responsibility; Liskov (sostituibile con
 * qualsiasi altra {@code InventoryView}).
 */
public class FxInventoryView implements InventoryView {

    private final VBox root = new VBox(12);
    private final ListView<ItemEntry> items = new ListView<>();
    private final Label capacity = new Label();
    private final Label heroName = new Label();
    private final ProgressBar health = Widgets.healthBar();
    private final Label healthText = new Label();
    private final Label status = Widgets.statusLabel();
    private InventoryPresenter presenter;

    public FxInventoryView() {
        Label title = new Label("Inventory");
        title.getStyleClass().add("title");
        heroName.getStyleClass().add("heading");

        items.getStyleClass().add("log");
        items.setPlaceholder(new Label("Your bag is empty"));
        items.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(ItemEntry item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.name() + "  -  " + item.description());
            }
        });
        items.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                useSelected();
            }
        });
        VBox.setVgrow(items, Priority.ALWAYS);

        HBox commands = new HBox(10,
                Widgets.button("Use", this::useSelected),
                Widgets.button("Back", () -> presenter.onBack()));

        VBox hero = new VBox(6, heroName, health, healthText);
        hero.getStyleClass().add("panel");
        hero.setMaxWidth(320);

        root.getChildren().addAll(title, status, hero, capacity, items, commands);
        root.getStyleClass().add("screen");
    }

    public void setPresenter(InventoryPresenter presenter) {
        this.presenter = Objects.requireNonNull(presenter, "presenter");
    }

    public Parent root() {
        return root;
    }

    @Override
    public void showItems(List<ItemEntry> entries, int maxItems) {
        Widgets.clearStatus(status);
        items.getItems().setAll(entries);
        capacity.setText("Items: " + entries.size() + " / " + maxItems);
    }

    @Override
    public void showHero(HeroStatus hero) {
        heroName.setText(hero.name() + "  (level " + hero.level() + ")");
        Widgets.updateHealth(health, healthText, hero.health(), hero.maxHealth());
    }

    @Override
    public void showError(String message) {
        Widgets.setStatus(status, message, true);
    }

    private void useSelected() {
        presenter.onUse(items.getSelectionModel().getSelectedIndex());
    }
}

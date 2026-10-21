package it.unicam.cs.mpgc.rpg123024.gui.javafx;

import it.unicam.cs.mpgc.rpg123024.gui.presenter.Navigator;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Implementazione JavaFX di {@link Navigator}: una sola {@link Scene} di cui si sostituisce la radice.
 * <p>
 * <b>Responsabilità:</b> tenere il registro delle schermate (radice grafica e azione da eseguire alla
 * visualizzazione, tipicamente il {@code refresh} del Presenter), mostrare quella richiesta e
 * notificare la fine della partita. Applica il foglio di stile comune.
 * <p>
 * <b>Pattern:</b> Adapter (porta di navigazione verso JavaFX) con Registry delle schermate.
 * <b>SOLID:</b> Single Responsibility (solo navigazione); Open/Closed (nuove schermate per
 * registrazione); Dependency Inversion (i Presenter conoscono solo l'interfaccia {@code Navigator}).
 */
public class FxNavigator implements Navigator {

    public enum Screen { MENU, EXPLORATION, COMBAT, INVENTORY }

    private static final String STYLESHEET = "dungeon.css";

    private final Scene scene;
    private final Map<Screen, Parent> roots = new EnumMap<>(Screen.class);
    private final Map<Screen, Runnable> onShow = new EnumMap<>(Screen.class);
    private Consumer<Boolean> gameEndHandler = victory -> { };

    public FxNavigator(Scene scene) {
        this.scene = Objects.requireNonNull(scene, "scene");
        scene.getStylesheets().add(
                Objects.requireNonNull(FxNavigator.class.getResource(STYLESHEET), STYLESHEET).toExternalForm());
    }

    public void register(Screen screen, Parent root, Runnable onShowAction) {
        roots.put(Objects.requireNonNull(screen, "screen"), Objects.requireNonNull(root, "root"));
        onShow.put(screen, Objects.requireNonNull(onShowAction, "onShowAction"));
    }

    public void setGameEndHandler(Consumer<Boolean> handler) {
        this.gameEndHandler = Objects.requireNonNull(handler, "handler");
    }

    @Override
    public void showMenu() {
        show(Screen.MENU);
    }

    @Override
    public void showExploration() {
        show(Screen.EXPLORATION);
    }

    @Override
    public void showCombat() {
        show(Screen.COMBAT);
    }

    @Override
    public void showInventory() {
        show(Screen.INVENTORY);
    }

    @Override
    public void showGameEnd(boolean victory) {
        show(Screen.MENU);
        gameEndHandler.accept(victory);
    }

    private void show(Screen screen) {
        Parent root = roots.get(screen);
        if (root == null) {
            throw new IllegalStateException("screen not registered: " + screen);
        }
        if (scene.getRoot() != root) {
            scene.setRoot(root);
        }
        onShow.get(screen).run();
        root.requestFocus();
    }
}

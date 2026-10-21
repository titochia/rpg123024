package it.unicam.cs.mpgc.rpg123024.gui.javafx;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;

import java.util.List;

/**
 * Componenti e operazioni grafiche ricorrenti delle View JavaFX.
 * <p>
 * <b>Responsabilità:</b> creare e aggiornare in modo uniforme barre della salute, registri degli
 * eventi, pulsanti ed etichette di stato (messaggi informativi o d'errore).
 * <p>
 * <b>SOLID:</b> Single Responsibility (solo costruzione di widget); DRY (stesso aspetto e
 * comportamento in tutte le schermate).
 */
final class Widgets {

    private Widgets() {
    }

    static Button button(String text, Runnable action) {
        Button button = new Button(text);
        button.setOnAction(event -> action.run());
        button.setFocusTraversable(false);
        return button;
    }

    static ProgressBar healthBar() {
        ProgressBar bar = new ProgressBar(1);
        bar.getStyleClass().add("health-bar");
        bar.setMaxWidth(Double.MAX_VALUE);
        return bar;
    }

    static void updateHealth(ProgressBar bar, Label text, int health, int maxHealth) {
        bar.setProgress(maxHealth == 0 ? 0 : (double) health / maxHealth);
        text.setText(health + " / " + maxHealth + " HP");
    }

    static ListView<String> logList() {
        ListView<String> log = new ListView<>();
        log.getStyleClass().add("log");
        log.setFocusTraversable(false);
        return log;
    }

    static void showLog(ListView<String> log, List<String> messages) {
        log.getItems().setAll(messages);
        if (!messages.isEmpty()) {
            log.scrollTo(messages.size() - 1);
        }
    }

    static Label statusLabel() {
        Label status = new Label();
        status.getStyleClass().add("status");
        status.setWrapText(true);
        return status;
    }

    static void setStatus(Label status, String text, boolean error) {
        status.getStyleClass().removeAll("error", "info");
        status.getStyleClass().add(error ? "error" : "info");
        status.setText(text);
    }

    static void clearStatus(Label status) {
        status.setText("");
    }
}

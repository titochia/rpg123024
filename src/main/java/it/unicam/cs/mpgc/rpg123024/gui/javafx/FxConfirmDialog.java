package it.unicam.cs.mpgc.rpg123024.gui.javafx;

import it.unicam.cs.mpgc.rpg123024.gui.view.ConfirmView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Implementazione JavaFX di {@link ConfirmView} tramite finestra di dialogo modale.
 * <p>
 * <b>Responsabilità:</b> mostrare una domanda sì/no e restituire la risposta dell'utente; è usata
 * dal menu e dalla richiesta di chiusura dell'applicazione.
 * <p>
 * <b>Pattern:</b> Adapter (contratto di conferma verso il dialogo JavaFX). <b>SOLID:</b> Single
 * Responsibility; Liskov (sostituibile con qualsiasi altra {@code ConfirmView}).
 */
public class FxConfirmDialog implements ConfirmView {

    @Override
    public boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle("Dungeon Crawler");
        alert.setHeaderText(null);
        return alert.showAndWait().filter(ButtonType.YES::equals).isPresent();
    }
}

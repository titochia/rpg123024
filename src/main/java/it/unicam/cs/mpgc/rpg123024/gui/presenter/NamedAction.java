package it.unicam.cs.mpgc.rpg123024.gui.presenter;

import it.unicam.cs.mpgc.rpg123024.domain.combat.TurnAction;

import java.util.Objects;

/**
 * Azione di combattimento dell'eroe associata al nome mostrato nell'interfaccia.
 * <p>
 * <b>Responsabilità:</b> collegare un Command di dominio ({@link TurnAction}) all'etichetta
 * visualizzata, così che la View lavori solo con nomi e il Presenter con i comandi.
 * <p>
 * <b>Pattern:</b> Value Object che incapsula un Command. <b>SOLID:</b> Open/Closed (l'elenco delle
 * azioni è iniettato dalla composition root: nuove azioni senza modificare Presenter o View).
 */
public record NamedAction(String name, TurnAction action) {

    public NamedAction {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(action, "action");
    }
}

package it.unicam.cs.mpgc.rpg123024.application.usecase;

import it.unicam.cs.mpgc.rpg123024.application.port.EventPublisher;
import it.unicam.cs.mpgc.rpg123024.application.state.GameSession;
import it.unicam.cs.mpgc.rpg123024.domain.model.Item;

import java.util.Objects;

/**
 * Use case: utilizzo di un oggetto dell'inventario.
 * <p>
 * <b>Responsabilità:</b> delegare alla fase corrente l'uso dell'oggetto. In esplorazione l'oggetto
 * è consumato subito; in combattimento consuma il turno: la differenza è incapsulata nelle fasi.
 * <p>
 * <b>SOLID:</b> Single Responsibility; Open/Closed (nessun condizionale sullo stato).
 */
public class UseItem {

    private final EventPublisher events;

    public UseItem(EventPublisher events) {
        this.events = Objects.requireNonNull(events, "events");
    }

    public void execute(GameSession session, Item item) {
        session.changePhase(session.phase().useItem(session, item, events));
    }
}
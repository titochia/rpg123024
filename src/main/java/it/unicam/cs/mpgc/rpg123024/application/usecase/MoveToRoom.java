package it.unicam.cs.mpgc.rpg123024.application.usecase;

import it.unicam.cs.mpgc.rpg123024.application.port.EventPublisher;
import it.unicam.cs.mpgc.rpg123024.application.state.GameSession;
import it.unicam.cs.mpgc.rpg123024.domain.map.Direction;

import java.util.Objects;

/**
 * Use case: spostamento dell'eroe in una stanza adiacente.
 * <p>
 * <b>Responsabilità:</b> delegare il comando alla fase corrente e aggiornare la sessione con la
 * fase restituita. Non contiene regole: queste dipendono dallo stato (pattern State).
 * <p>
 * <b>SOLID:</b> Single Responsibility; Open/Closed (nuove fasi non richiedono modifiche qui).
 */
public class MoveToRoom {

    private final EventPublisher events;

    public MoveToRoom(EventPublisher events) {
        this.events = Objects.requireNonNull(events, "events");
    }

    public void execute(GameSession session, Direction direction) {
        session.changePhase(session.phase().move(session, direction, events));
    }
}
package it.unicam.cs.mpgc.rpg123024.application.usecase;

import it.unicam.cs.mpgc.rpg123024.application.port.EventPublisher;
import it.unicam.cs.mpgc.rpg123024.application.state.GameSession;
import it.unicam.cs.mpgc.rpg123024.domain.combat.TurnAction;

import java.util.Objects;

/**
 * Use case: esecuzione del turno del giocatore con l'azione scelta.
 * <p>
 * <b>Responsabilità:</b> delegare alla fase corrente l'azione ({@link TurnAction}) e aggiornare la
 * fase della sessione. La risposta del mostro e la conclusione dello scontro sono gestite dalla fase
 * di combattimento.
 * <p>
 * <b>SOLID:</b> Single Responsibility; Dependency Inversion (dipende dall'astrazione
 * {@code TurnAction}, quindi la GUI può comporre qualsiasi azione).
 */
public class ExecuteTurn {

    private final EventPublisher events;

    public ExecuteTurn(EventPublisher events) {
        this.events = Objects.requireNonNull(events, "events");
    }

    public void execute(GameSession session, TurnAction action) {
        session.changePhase(session.phase().heroAct(session, action, events));
    }
}
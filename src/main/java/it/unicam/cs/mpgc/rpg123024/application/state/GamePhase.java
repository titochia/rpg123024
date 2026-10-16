package it.unicam.cs.mpgc.rpg123024.application.state;

import it.unicam.cs.mpgc.rpg123024.application.port.EventPublisher;
import it.unicam.cs.mpgc.rpg123024.domain.combat.TurnAction;
import it.unicam.cs.mpgc.rpg123024.domain.map.Direction;
import it.unicam.cs.mpgc.rpg123024.domain.model.Item;

/**
 * Stato astratto del flusso di gioco (pattern State): decide quali comandi sono ammessi e come
 * vengono eseguiti, restituendo la fase successiva.
 * <p>
 * <b>Responsabilità:</b> definire i comandi del giocatore ({@code move}, {@code heroAct},
 * {@code useItem}); per default ogni comando è vietato, e ogni fase concreta abilita solo ciò che
 * le compete.
 * <p>
 * <b>SOLID:</b> Open/Closed (nuove fasi senza toccare use case né sessione); Liskov (fasi
 * intercambiabili); elimina {@code if/switch} sullo stato nei chiamanti. Il metodo {@link #id()} è
 * l'identificativo stabile utilizzabile dalla persistenza.
 */
public interface GamePhase {

    String id();

    default GamePhase move(GameSession session, Direction direction, EventPublisher events) {
        throw notAllowed("move");
    }

    default GamePhase heroAct(GameSession session, TurnAction action, EventPublisher events) {
        throw notAllowed("act");
    }

    default GamePhase useItem(GameSession session, Item item, EventPublisher events) {
        throw notAllowed("use item");
    }

    private IllegalStateException notAllowed(String operation) {
        return new IllegalStateException(operation + " not allowed in phase " + id());
    }
}
package it.unicam.cs.mpgc.rpg123024.application.port;

import it.unicam.cs.mpgc.rpg123024.domain.event.GameEvent;

/**
 * Porta in uscita per la notifica degli eventi di gioco.
 * <p>
 * <b>Responsabilità:</b> definire come il core pubblica un {@link GameEvent} senza sapere chi lo
 * riceverà (GUI, log, client remoto).
 * <p>
 * <b>Pattern:</b> Observer (lato publisher) / Ports &amp; Adapters. <b>SOLID:</b> Dependency
 * Inversion (use case e fasi dipendono da questa interfaccia, l'adapter è fornito dall'esterno);
 * Interface Segregation (un solo metodo).
 */
@FunctionalInterface
public interface EventPublisher {

    void publish(GameEvent event);
}
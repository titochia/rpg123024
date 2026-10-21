package it.unicam.cs.mpgc.rpg123024.gui.event;

import it.unicam.cs.mpgc.rpg123024.application.port.EventPublisher;
import it.unicam.cs.mpgc.rpg123024.domain.event.GameEvent;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Bus degli eventi di gioco: adapter della porta {@link EventPublisher} lato interfaccia utente.
 * <p>
 * <b>Responsabilità:</b> registrare gli osservatori ({@link Consumer} di {@link GameEvent}) e
 * notificare a ciascuno ogni evento pubblicato dagli use case, nell'ordine di sottoscrizione.
 * Non interpreta gli eventi.
 * <p>
 * <b>Pattern:</b> Observer (subject) / Adapter della porta in uscita. <b>SOLID:</b> Dependency
 * Inversion (il core pubblica tramite la porta senza conoscere la GUI); Open/Closed (nuovi
 * osservatori, ad esempio log su file o client remoto, senza modificare publisher né core);
 * Single Responsibility (solo smistamento). La lista copy-on-write consente di sottoscriversi o
 * annullare la sottoscrizione durante una notifica.
 */
public class GameEventBus implements EventPublisher {

    private final List<Consumer<GameEvent>> subscribers = new CopyOnWriteArrayList<>();

    public void subscribe(Consumer<GameEvent> subscriber) {
        subscribers.add(Objects.requireNonNull(subscriber, "subscriber"));
    }

    public void unsubscribe(Consumer<GameEvent> subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public void publish(GameEvent event) {
        Objects.requireNonNull(event, "event");
        subscribers.forEach(subscriber -> subscriber.accept(event));
    }
}

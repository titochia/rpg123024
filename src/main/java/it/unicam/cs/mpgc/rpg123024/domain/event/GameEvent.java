package it.unicam.cs.mpgc.rpg123024.domain.event;

import it.unicam.cs.mpgc.rpg123024.domain.combat.ActionResult;
import it.unicam.cs.mpgc.rpg123024.domain.combat.CombatEngine;
import it.unicam.cs.mpgc.rpg123024.domain.map.Position;
import it.unicam.cs.mpgc.rpg123024.domain.model.Item;

/**
 * Eventi di gioco immutabili emessi durante l'esecuzione degli use case.
 * <p>
 * <b>Responsabilità:</b> descrivere, in forma neutra rispetto alla UI, ciò che è accaduto
 * (ingresso in stanza, bottino, combattimento, avanzamento di livello, fine partita).
 * <p>
 * <b>Pattern:</b> Observer (gli eventi sono il messaggio notificato agli osservatori). <b>SOLID:</b>
 * Open/Closed e Dependency Inversion (il core non conosce chi reagisce agli eventi); l'interfaccia
 * sealed rende esaustivi i {@code switch} dei consumatori.
 */
public sealed interface GameEvent {

    record RoomEntered(Position position) implements GameEvent { }

    record ItemCollected(Item item) implements GameEvent { }

    record CombatStarted(String monsterName) implements GameEvent { }

    record ActionPerformed(ActionResult result) implements GameEvent { }

    record CombatEnded(CombatEngine.Outcome outcome) implements GameEvent { }

    record LevelUp(int newLevel) implements GameEvent { }

    record GameWon() implements GameEvent { }

    record GameOver() implements GameEvent { }
}
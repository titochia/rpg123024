package it.unicam.cs.mpgc.rpg123024.application.state;

import it.unicam.cs.mpgc.rpg123024.application.port.EventPublisher;
import it.unicam.cs.mpgc.rpg123024.domain.combat.ActionResult;
import it.unicam.cs.mpgc.rpg123024.domain.combat.CombatEngine;
import it.unicam.cs.mpgc.rpg123024.domain.combat.Fighter;
import it.unicam.cs.mpgc.rpg123024.domain.combat.UseItemAction;
import it.unicam.cs.mpgc.rpg123024.domain.event.GameEvent;
import it.unicam.cs.mpgc.rpg123024.domain.map.Direction;
import it.unicam.cs.mpgc.rpg123024.domain.map.Position;
import it.unicam.cs.mpgc.rpg123024.domain.map.Room;
import it.unicam.cs.mpgc.rpg123024.domain.model.Item;
import it.unicam.cs.mpgc.rpg123024.domain.model.Monster;

import java.util.Optional;

/**
 * Fase di esplorazione: il giocatore si muove tra le stanze e usa oggetti.
 * <p>
 * <b>Responsabilità:</b> eseguire lo spostamento (validazione dei confini, visita, raccolta del
 * bottino), avviare un combattimento se la stanza contiene un mostro, o dichiarare la vittoria
 * se si raggiunge l'uscita. Non gestisce i turni di combattimento ({@link CombatPhase}).
 * <p>
 * <b>Pattern:</b> State concreto (senza stato proprio, quindi singleton). <b>SOLID:</b> Single
 * Responsibility (regole dell'esplorazione); i passaggi ad altre fasi avvengono restituendole.
 */
public final class ExploringPhase implements GamePhase {

    static final ExploringPhase INSTANCE = new ExploringPhase();

    private ExploringPhase() {
    }

    @Override
    public String id() {
        return "EXPLORING";
    }

    @Override
    public GamePhase move(GameSession session, Direction direction, EventPublisher events) {
        Position target = session.position().move(direction);
        if (!session.dungeon().contains(target)) {
            throw new IllegalArgumentException("no room towards " + direction);
        }
        session.moveTo(target);
        Room room = session.currentRoom();
        room.markVisited();
        events.publish(new GameEvent.RoomEntered(target));
        collectLoot(session, room, events);

        Optional<Monster> monster = room.monster();
        if (monster.isPresent()) {
            session.startCombat(new CombatEngine(session.hero(), monster.get(), session.monsterAI()));
            events.publish(new GameEvent.CombatStarted(monster.get().name()));
            return CombatPhase.INSTANCE.resolve(session, events);
        }
        if (target.equals(session.dungeon().exit())) {
            events.publish(new GameEvent.GameWon());
            return VictoryPhase.INSTANCE;
        }
        return this;
    }

    @Override
    public GamePhase useItem(GameSession session, Item item, EventPublisher events) {
        Fighter hero = new Fighter(session.hero());
        ActionResult result = new UseItemAction(session.hero(), item).execute(hero, hero);
        events.publish(new GameEvent.ActionPerformed(result));
        return this;
    }

    private static void collectLoot(GameSession session, Room room, EventPublisher events) {
        for (Item item : room.collectLoot()) {
            if (session.hero().inventory().add(item)) {
                events.publish(new GameEvent.ItemCollected(item));
            } else {
                room.addLoot(item);
            }
        }
    }
}
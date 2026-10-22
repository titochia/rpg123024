package it.unicam.cs.mpgc.rpg123024.application.state;

import it.unicam.cs.mpgc.rpg123024.application.port.EventPublisher;
import it.unicam.cs.mpgc.rpg123024.domain.combat.ActionResult;
import it.unicam.cs.mpgc.rpg123024.domain.combat.CombatEngine;
import it.unicam.cs.mpgc.rpg123024.domain.combat.TurnAction;
import it.unicam.cs.mpgc.rpg123024.domain.combat.UseItemAction;
import it.unicam.cs.mpgc.rpg123024.domain.event.GameEvent;
import it.unicam.cs.mpgc.rpg123024.domain.map.Room;
import it.unicam.cs.mpgc.rpg123024.domain.model.Item;
import it.unicam.cs.mpgc.rpg123024.domain.model.Monster;

import java.util.List;

/**
 * Fase di combattimento: il giocatore sceglie azioni, il mostro risponde tramite la sua AI.
 * <p>
 * <b>Responsabilità:</b> inoltrare le azioni al {@link CombatEngine}, pubblicare gli esiti, far
 * agire il mostro finché non torna il turno dell'eroe e concludere lo scontro (esperienza, livelli,
 * rimozione del mostro, passaggio a esplorazione, vittoria o sconfitta). Assegnare ricompense è qui
 * (non nel motore) per mantenere il motore privo di regole di progressione. Un'azione non disponibile
 * (ad esempio in ricarica) viene rifiutata prima di interpellare il motore, così il turno non viene
 * consumato e gli effetti periodici non avanzano.
 * <p>
 * <b>Pattern:</b> State concreto (singleton senza stato). <b>SOLID:</b> Single Responsibility
 * (regole del flusso di combattimento); l'uso di oggetti riusa il Command esistente (DRY).
 */
public final class CombatPhase implements GamePhase {

    static final CombatPhase INSTANCE = new CombatPhase();

    private CombatPhase() {
    }

    @Override
    public String id() {
        return "COMBAT";
    }

    @Override
    public GamePhase heroAct(GameSession session, TurnAction action, EventPublisher events) {
        CombatEngine combat = engine(session);
        if (!action.isAvailableFor(combat.heroCombatant())) {
            throw new IllegalStateException("This action is still recharging");
        }
        publishAll(combat.heroAct(action), events);
        return resolve(session, events);
    }

    @Override
    public GamePhase useItem(GameSession session, Item item, EventPublisher events) {
        return heroAct(session, new UseItemAction(session.hero(), item), events);
    }

    GamePhase resolve(GameSession session, EventPublisher events) {
        CombatEngine combat = engine(session);
        while (!combat.isOver() && !combat.isHeroTurn()) {
            publishAll(combat.monsterAct(), events);
        }
        return combat.isOver() ? conclude(session, combat, events) : this;
    }

    private GamePhase conclude(GameSession session, CombatEngine combat, EventPublisher events) {
        CombatEngine.Outcome outcome = combat.outcome();
        session.endCombat();
        events.publish(new GameEvent.CombatEnded(outcome));
        if (outcome == CombatEngine.Outcome.HERO_DEFEATED) {
            events.publish(new GameEvent.GameOver());
            return DefeatPhase.INSTANCE;
        }
        Room room = session.currentRoom();
        Monster monster = room.monster().orElseThrow(IllegalStateException::new);
        room.clearMonster();
        if (session.hero().gainExperience(monster.experienceReward()) > 0) {
            events.publish(new GameEvent.LevelUp(session.hero().level()));
        }
        if (session.position().equals(session.dungeon().exit())) {
            events.publish(new GameEvent.GameWon());
            return VictoryPhase.INSTANCE;
        }
        return ExploringPhase.INSTANCE;
    }

    private static CombatEngine engine(GameSession session) {
        return session.combat().orElseThrow(() -> new IllegalStateException("no combat in progress"));
    }

    private static void publishAll(List<ActionResult> results, EventPublisher events) {
        results.forEach(result -> events.publish(new GameEvent.ActionPerformed(result)));
    }
}
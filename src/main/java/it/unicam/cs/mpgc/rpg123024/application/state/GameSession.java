package it.unicam.cs.mpgc.rpg123024.application.state;

import it.unicam.cs.mpgc.rpg123024.domain.combat.CombatEngine;
import it.unicam.cs.mpgc.rpg123024.domain.combat.MonsterAI;
import it.unicam.cs.mpgc.rpg123024.domain.map.Dungeon;
import it.unicam.cs.mpgc.rpg123024.domain.map.Position;
import it.unicam.cs.mpgc.rpg123024.domain.map.Room;
import it.unicam.cs.mpgc.rpg123024.domain.model.Hero;

import java.util.Objects;
import java.util.Optional;

/**
 * Stato aggregato di una partita: eroe, dungeon, posizione, eventuale combattimento e fase corrente.
 * <p>
 * <b>Responsabilità:</b> custodire lo stato mutabile della partita e fornire accesso controllato ai
 * suoi elementi. Non contiene regole di gioco: sono nelle {@link GamePhase} e nel Domain.
 * <p>
 * <b>Pattern:</b> contesto del pattern State. <b>Architettura:</b> è l'unità che i
 * {@code GameRepository} salvano e ripristinano; la sua posizione nell'Application layer mantiene
 * il Domain indipendente dalle fasi di flusso.
 */
public class GameSession {

    private final Hero hero;
    private final Dungeon dungeon;
    private final MonsterAI monsterAI;
    private Position position;
    private CombatEngine combat;
    private GamePhase phase;

    public GameSession(Hero hero, Dungeon dungeon, MonsterAI monsterAI) {
        this.hero = Objects.requireNonNull(hero, "hero");
        this.dungeon = Objects.requireNonNull(dungeon, "dungeon");
        this.monsterAI = Objects.requireNonNull(monsterAI, "monsterAI");
        this.position = dungeon.start();
        this.phase = ExploringPhase.INSTANCE;
        currentRoom().markVisited();
    }

    public Hero hero() {
        return hero;
    }

    public Dungeon dungeon() {
        return dungeon;
    }

    public MonsterAI monsterAI() {
        return monsterAI;
    }

    public Position position() {
        return position;
    }

    public Room currentRoom() {
        return dungeon.room(position);
    }

    public Optional<CombatEngine> combat() {
        return Optional.ofNullable(combat);
    }

    public GamePhase phase() {
        return phase;
    }

    void moveTo(Position newPosition) {
        this.position = Objects.requireNonNull(newPosition, "newPosition");
    }

    void startCombat(CombatEngine engine) {
        this.combat = Objects.requireNonNull(engine, "engine");
    }

    void endCombat() {
        this.combat = null;
    }

    public void changePhase(GamePhase newPhase) {
        this.phase = Objects.requireNonNull(newPhase, "newPhase");
    }
}
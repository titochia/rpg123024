package it.unicam.cs.mpgc.rpg123024.domain.map;

import it.unicam.cs.mpgc.rpg123024.domain.model.Item;
import it.unicam.cs.mpgc.rpg123024.domain.model.Monster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Singola stanza del dungeon: contiene al più un mostro, del bottino e lo stato di visita.
 * <p>
 * <b>Responsabilità:</b> custodire il contenuto della stanza e mutarlo in modo controllato
 * (piazzare/rimuovere il mostro, raccogliere il bottino). Non conosce i vicini né il flusso di
 * gioco.
 * <p>
 * <b>SOLID:</b> Single Responsibility (solo il contenuto di una cella); Law of Demeter: espone
 * viste non modificabili e {@link Optional} invece di valori nulli.
 */
public class Room {

    private final Position position;
    private final List<Item> loot = new ArrayList<>();
    private Monster monster;
    private boolean visited;

    public Room(Position position) {
        this.position = Objects.requireNonNull(position, "position");
    }

    public Position position() {
        return position;
    }

    public Optional<Monster> monster() {
        return Optional.ofNullable(monster);
    }

    public void placeMonster(Monster monster) {
        this.monster = Objects.requireNonNull(monster, "monster");
    }

    public void clearMonster() {
        this.monster = null;
    }

    public void addLoot(Item item) {
        loot.add(Objects.requireNonNull(item, "item"));
    }

    public List<Item> loot() {
        return Collections.unmodifiableList(loot);
    }

    public List<Item> collectLoot() {
        List<Item> collected = List.copyOf(loot);
        loot.clear();
        return collected;
    }

    public boolean isVisited() {
        return visited;
    }

    public void markVisited() {
        visited = true;
    }
}
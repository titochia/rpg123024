package it.unicam.cs.mpgc.rpg123024.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Contenitore a capacità limitata di {@link Item}.
 * <p>
 * <b>Responsabilità:</b> gestire aggiunta, rimozione e consultazione degli oggetti rispettando
 * il limite di capacità. Non conosce gli effetti degli oggetti né chi li possiede.
 * <p>
 * <b>Architettura:</b> componente del Domain composto da {@link Hero}; espone solo una vista
 * non modificabile degli oggetti per preservare l'incapsulamento.
 */
public class Inventory {

    private final int capacity;
    private final List<Item> items = new ArrayList<>();

    /** @param capacity numero massimo di oggetti (strettamente positivo) */
    public Inventory(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be > 0");
        }
        this.capacity = capacity;
    }

    /**
     * @param item oggetto da aggiungere
     * @return {@code true} se aggiunto, {@code false} se l'inventario è pieno
     */
    public boolean add(Item item) {
        Objects.requireNonNull(item, "item");
        if (isFull()) {
            return false;
        }
        return items.add(item);
    }

    /**
     * Rimuove una occorrenza dell'oggetto.
     *
     * @param item oggetto da rimuovere
     * @return {@code true} se presente e rimosso
     */
    public boolean remove(Item item) {
        return items.remove(item);
    }

    /** @return {@code true} se l'oggetto è presente */
    public boolean contains(Item item) {
        return items.contains(item);
    }

    /** @return {@code true} se è stata raggiunta la capacità massima */
    public boolean isFull() {
        return items.size() >= capacity;
    }

    /** @return numero di oggetti contenuti */
    public int size() {
        return items.size();
    }

    /** @return capacità massima */
    public int capacity() {
        return capacity;
    }

    /** @return vista non modificabile degli oggetti */
    public List<Item> items() {
        return Collections.unmodifiableList(items);
    }
}
package it.unicam.cs.mpgc.rpg123024.domain.model;

import java.util.Objects;

/**
 * Personaggio controllato dal giocatore.
 * <p>
 * <b>Responsabilità:</b> estendere {@link AbstractCombatant} con progressione (livello ed
 * esperienza) e possesso di un {@link Inventory}, consentendo l'uso di oggetti. Non gestisce
 * input, rendering né persistenza.
 * <p>
 * <b>Architettura:</b> entità centrale del Domain, manipolata dagli use case del layer
 * Application. Le regole numeriche di avanzamento sono costanti locali, isolate in un unico punto.
 */
public class Hero extends AbstractCombatant {

    private static final int DEFAULT_INVENTORY_CAPACITY = 10;
    private static final int XP_PER_LEVEL = 100;
    private static final Stats LEVEL_UP_GROWTH = new Stats(10, 2, 1, 1);

    private final Inventory inventory;
    private int level = 1;
    private int experience;

    /**
     * Crea un eroe di livello 1 con inventario di capacità predefinita.
     *
     * @param name      nome dell'eroe
     * @param baseStats statistiche iniziali
     */
    public Hero(String name, Stats baseStats) {
        this(name, baseStats, DEFAULT_INVENTORY_CAPACITY);
    }

    /**
     * @param name              nome dell'eroe
     * @param baseStats         statistiche iniziali
     * @param inventoryCapacity capacità dell'inventario
     */
    public Hero(String name, Stats baseStats, int inventoryCapacity) {
        super(name, baseStats);
        this.inventory = new Inventory(inventoryCapacity);
    }

    /** @return livello corrente (parte da 1) */
    public int level() {
        return level;
    }

    /** @return esperienza accumulata nel livello corrente */
    public int experience() {
        return experience;
    }

    /** @return esperienza necessaria per raggiungere il livello successivo */
    public int experienceToNextLevel() {
        return level * XP_PER_LEVEL;
    }

    /** @return inventario dell'eroe */
    public Inventory inventory() {
        return inventory;
    }

    /**
     * Assegna esperienza ed esegue gli eventuali avanzamenti di livello.
     *
     * @param amount esperienza guadagnata (non negativa)
     * @return numero di livelli guadagnati
     */
    public int gainExperience(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be >= 0");
        }
        experience += amount;
        int gained = 0;
        while (experience >= experienceToNextLevel()) {
            experience -= experienceToNextLevel();
            level++;
            growBaseStats(LEVEL_UP_GROWTH);
            gained++;
        }
        return gained;
    }

    /**
     * Consuma un oggetto dall'inventario applicandone l'effetto all'eroe.
     *
     * @param item oggetto da usare
     * @return {@code true} se l'oggetto era presente ed è stato usato
     */
    public boolean useItem(Item item) {
        Objects.requireNonNull(item, "item");
        if (!inventory.remove(item)) {
            return false;
        }
        item.use(this);
        return true;
    }
}
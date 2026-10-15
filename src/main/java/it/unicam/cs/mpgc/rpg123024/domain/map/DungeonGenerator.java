package it.unicam.cs.mpgc.rpg123024.domain.map;

/**
 * Astrazione dell'algoritmo di creazione di un {@link Dungeon} popolato.
 * <p>
 * <b>Responsabilità:</b> definire il contratto di generazione (nessun parametro: la
 * configurazione appartiene all'implementazione).
 * <p>
 * <b>Pattern:</b> Strategy/Factory. <b>SOLID:</b> Open/Closed (nuovi algoritmi, ad esempio mappe
 * da file o livelli fissi, senza modificare i chiamanti); Dependency Inversion (gli use case
 * dipendono da questa interfaccia).
 */
public interface DungeonGenerator {

    Dungeon generate();
}
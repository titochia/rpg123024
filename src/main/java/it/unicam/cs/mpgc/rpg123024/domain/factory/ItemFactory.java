package it.unicam.cs.mpgc.rpg123024.domain.factory;

import it.unicam.cs.mpgc.rpg123024.domain.model.Item;
import it.unicam.cs.mpgc.rpg123024.domain.random.RandomSource;

/**
 * Factory di {@link Item}.
 * <p>
 * <b>Responsabilità:</b> incapsulare la creazione casuale degli oggetti di bottino durante la
 * generazione del dungeon. La ricostruzione di un oggetto dal suo {@code id} durante il caricamento è
 * invece compito di {@link ItemCatalog} (astrazioni separate, Interface Segregation).
 * <p>
 * <b>Pattern:</b> Factory. <b>SOLID:</b> Dependency Inversion e Open/Closed come per
 * {@link MonsterFactory}.
 */
@FunctionalInterface
public interface ItemFactory {

    Item create(RandomSource random);
}
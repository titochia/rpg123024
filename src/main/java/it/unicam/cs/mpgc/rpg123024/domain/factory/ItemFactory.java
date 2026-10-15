package it.unicam.cs.mpgc.rpg123024.domain.factory;

import it.unicam.cs.mpgc.rpg123024.domain.model.Item;
import it.unicam.cs.mpgc.rpg123024.domain.random.RandomSource;

/**
 * Factory di {@link Item}.
 * <p>
 * <b>Responsabilità:</b> incapsulare la creazione degli oggetti di bottino; in futuro sarà anche
 * il punto in cui ricostruire un oggetto dal suo {@code id} durante il caricamento di un salvataggio.
 * <p>
 * <b>Pattern:</b> Factory. <b>SOLID:</b> Dependency Inversion e Open/Closed come per
 * {@link MonsterFactory}.
 */
@FunctionalInterface
public interface ItemFactory {

    Item create(RandomSource random);
}
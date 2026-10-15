package it.unicam.cs.mpgc.rpg123024.domain.factory;

import it.unicam.cs.mpgc.rpg123024.domain.model.Monster;
import it.unicam.cs.mpgc.rpg123024.domain.random.RandomSource;

/**
 * Factory di {@link Monster}.
 * <p>
 * <b>Responsabilità:</b> incapsulare la creazione (e la scelta casuale del tipo) dei mostri.
 * <p>
 * <b>Pattern:</b> Factory. <b>SOLID:</b> Dependency Inversion (il generatore dipende
 * da questa astrazione, non da mostri concreti); Open/Closed (nuovi mostri senza toccare il
 * generatore). Interfaccia funzionale: nei test basta una lambda.
 */
@FunctionalInterface
public interface MonsterFactory {

    Monster create(RandomSource random);
}
package it.unicam.cs.mpgc.rpg123024.domain.random;

import java.util.Random;

/**
 * Implementazione di {@link RandomSource} basata su {@link Random} con seed esplicito.
 * <p>
 * <b>Responsabilità:</b> produrre sequenze riproducibili a partire da un seed, requisito per
 * test deterministici e per rigenerare lo stesso dungeon (utile anche per la persistenza:
 * basta salvare il seed).
 * <p>
 * <b>SOLID:</b> Single Responsibility (solo adattamento di {@code Random}); sostituibile
 * con altre implementazioni (Liskov, Dependency Inversion).
 */
public class SeededRandomSource implements RandomSource {

    private final Random random;

    public SeededRandomSource(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public int nextInt(int bound) {
        return random.nextInt(bound);
    }
}
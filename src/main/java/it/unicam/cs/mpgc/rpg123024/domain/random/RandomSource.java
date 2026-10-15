package it.unicam.cs.mpgc.rpg123024.domain.random;

/**
 * Porta di accesso alla casualità per il Domain.
 * <p>
 * <b>Responsabilità:</b> astrarre la sorgente di numeri casuali così che generazione e regole di
 * gioco non dipendano da un generatore concreto.
 * <p>
 * <b>SOLID:</b> Dependency Inversion (il Domain dipende da questa astrazione, non da
 * {@code java.util.Random}); Interface Segregation (contratto minimo). Rende il comportamento
 * deterministico e testabile (seed fisso o implementazioni fittizie nei test).
 */
public interface RandomSource {

    int nextInt(int bound);

    default boolean chance(double probability) {
        return nextInt(1_000_000) < probability * 1_000_000;
    }
}
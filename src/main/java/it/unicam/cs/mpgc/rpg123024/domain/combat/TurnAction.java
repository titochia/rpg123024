package it.unicam.cs.mpgc.rpg123024.domain.combat;

/**
 * Azione eseguibile durante un turno, incapsulata come oggetto (pattern Command).
 * <p>
 * <b>Responsabilità:</b> definire il contratto di esecuzione di una scelta del turno, restituendo
 * un {@link ActionResult}. Le azioni sono prodotte dal giocatore (via GUI) o da un {@link MonsterAI}
 * e sono indistinguibili per il motore.
 * <p>
 * <b>Pattern:</b> Command. <b>SOLID:</b> Open/Closed (nuove azioni, ad esempio la fuga, senza
 * modificare {@link CombatEngine}); Dependency Inversion (il motore dipende solo da questa
 * astrazione); Liskov (azioni intercambiabili).
 */
@FunctionalInterface
public interface TurnAction {

    ActionResult execute(Fighter actor, Fighter opponent);
}
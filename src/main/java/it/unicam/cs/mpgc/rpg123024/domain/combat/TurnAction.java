package it.unicam.cs.mpgc.rpg123024.domain.combat;

import it.unicam.cs.mpgc.rpg123024.domain.model.Combatant;

/**
 * Azione eseguibile durante un turno, incapsulata come oggetto (pattern Command).
 * <p>
 * <b>Responsabilità:</b> definire il contratto di esecuzione di una scelta del turno, restituendo
 * un {@link ActionResult}. Le azioni sono prodotte dal giocatore (via GUI) o da un {@link MonsterAI}
 * e sono indistinguibili per il motore. {@link #isAvailableFor(Combatant)} permette a un'azione di
 * dichiararsi temporaneamente non disponibile (per default lo è sempre).
 * <p>
 * <b>Pattern:</b> Command. <b>SOLID:</b> Open/Closed (nuove azioni, ad esempio la fuga, senza
 * modificare {@link CombatEngine}; il metodo di disponibilità ha un'implementazione di default, quindi
 * le azioni esistenti restano invariate); Dependency Inversion (il motore dipende solo da questa
 * astrazione); Liskov (azioni intercambiabili).
 */
@FunctionalInterface
public interface TurnAction {

    ActionResult execute(Fighter actor, Fighter opponent);

    default boolean isAvailableFor(Combatant actor) {
        return true;
    }
}
package it.unicam.cs.mpgc.rpg123024.domain.ability;

import it.unicam.cs.mpgc.rpg123024.domain.model.Combatant;

/**
 * Strategia di azione eseguibile da un {@link Combatant} su un bersaglio (pattern Strategy).
 * <p>
 * <b>Responsabilità:</b> definire il contratto comune di ogni abilità (nome ed esecuzione),
 * restituendo l'entità numerica effettivamente prodotta (danno inflitto o salute recuperata).
 * <p>
 * <b>SOLID:</b> Open/Closed (nuove abilità = nuove implementazioni, nessuna modifica al motore di
 * combattimento); Dependency Inversion (il motore dipende solo da questa astrazione);
 * Interface Segregation (contratto minimo, due metodi).
 */
public interface Ability {

    String name();

    int use(Combatant user, Combatant target);
}
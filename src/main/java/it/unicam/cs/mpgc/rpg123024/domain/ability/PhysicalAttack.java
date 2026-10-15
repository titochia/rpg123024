package it.unicam.cs.mpgc.rpg123024.domain.ability;

import it.unicam.cs.mpgc.rpg123024.domain.model.Combatant;

/**
 * Abilità di attacco fisico: il danno è l'attacco dell'utilizzatore ridotto dalla difesa del
 * bersaglio, con un minimo garantito di 1.
 * <p>
 * <b>Pattern:</b> Strategy concreta. <b>SOLID:</b> Single Responsibility (solo la formula del
 * danno fisico); legge le statistiche tramite {@code stats()}, quindi tiene automaticamente conto
 * degli effetti di stato decorati (Liskov).
 */
public class PhysicalAttack implements Ability {

    @Override
    public String name() {
        return "Attack";
    }

    @Override
    public int use(Combatant user, Combatant target) {
        int damage = Math.max(1, user.stats().attack() - target.stats().defense());
        return target.takeDamage(damage);
    }
}
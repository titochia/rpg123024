package it.unicam.cs.mpgc.rpg123024.domain.combat;

import it.unicam.cs.mpgc.rpg123024.domain.ability.Ability;
import it.unicam.cs.mpgc.rpg123024.domain.ability.PhysicalAttack;

/**
 * Intelligenza artificiale elementare: il mostro attacca sempre l'avversario con la stessa abilità.
 * <p>
 * <b>Pattern:</b> Strategy concreta di {@link MonsterAI}. <b>SOLID:</b> Single Responsibility
 * (una sola politica di scelta); l'abilità è iniettabile (Open/Closed, Dependency Inversion).
 */
public class AggressiveMonsterAI implements MonsterAI {

    private final TurnAction attack;

    public AggressiveMonsterAI() {
        this(new PhysicalAttack());
    }

    public AggressiveMonsterAI(Ability ability) {
        this.attack = new AbilityAction(ability, Targeting.OPPONENT);
    }

    @Override
    public TurnAction chooseAction(Fighter self, Fighter opponent) {
        return attack;
    }
}
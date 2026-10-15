package it.unicam.cs.mpgc.rpg123024.domain.effect;

import it.unicam.cs.mpgc.rpg123024.domain.model.Combatant;

/**
 * Effetto di stato che infligge un danno fisso all'inizio di ogni turno, ignorando la difesa.
 * <p>
 * <b>Pattern:</b> Decorator concreto che ridefinisce solo {@link #applyTurnEffect()} (Template
 * Method di {@link StatusEffect}). <b>SOLID:</b> Single Responsibility (solo il danno periodico);
 * Open/Closed (aggiunto senza modificare il motore, che si limita a invocare {@code onTurnStart()}).
 */
public class PoisonEffect extends StatusEffect {

    private final int damagePerTurn;

    public PoisonEffect(Combatant delegate, int damagePerTurn, int duration) {
        super(delegate, duration);
        if (damagePerTurn <= 0) {
            throw new IllegalArgumentException("damagePerTurn must be > 0");
        }
        this.damagePerTurn = damagePerTurn;
    }

    @Override
    public String effectName() {
        return "Poison";
    }

    @Override
    protected void applyTurnEffect() {
        delegate().takeDamage(damagePerTurn);
    }
}
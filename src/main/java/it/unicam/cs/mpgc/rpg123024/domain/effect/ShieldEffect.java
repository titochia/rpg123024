package it.unicam.cs.mpgc.rpg123024.domain.effect;

import it.unicam.cs.mpgc.rpg123024.domain.model.Combatant;
import it.unicam.cs.mpgc.rpg123024.domain.model.Stats;

/**
 * Effetto di stato che aumenta la difesa del combattente decorato per un numero di turni.
 * <p>
 * <b>Pattern:</b> Decorator concreto che ridefinisce solo {@link #stats()}. <b>SOLID:</b>
 * Single Responsibility (solo il modificatore di difesa); poiché {@code PhysicalAttack} legge le
 * statistiche tramite l'interfaccia, il bonus ha effetto senza alcuna modifica alle abilità.
 */
public class ShieldEffect extends StatusEffect {

    private final int defenseBonus;

    public ShieldEffect(Combatant delegate, int defenseBonus, int duration) {
        super(delegate, duration);
        if (defenseBonus <= 0) {
            throw new IllegalArgumentException("defenseBonus must be > 0");
        }
        this.defenseBonus = defenseBonus;
    }

    @Override
    public String effectName() {
        return "Shield";
    }

    @Override
    public Stats stats() {
        Stats base = delegate().stats();
        if (isExpired()) {
            return base;
        }
        return new Stats(base.maxHealth(), base.attack(), base.defense() + defenseBonus, base.speed());
    }
}
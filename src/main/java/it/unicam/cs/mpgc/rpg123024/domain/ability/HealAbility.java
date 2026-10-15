package it.unicam.cs.mpgc.rpg123024.domain.ability;

import it.unicam.cs.mpgc.rpg123024.domain.model.Combatant;

import java.util.Objects;

/**
 * Abilità di cura parametrica: ripristina una quantità fissa di salute al bersaglio.
 * <p>
 * <b>Pattern:</b> Strategy concreta, configurata via costruttore (nome e potenza) per coprire più
 * incantesimi di cura con un'unica classe. <b>SOLID:</b> Single Responsibility (solo la
 * logica di cura); Open/Closed (nuove cure = nuove istanze, non nuove modifiche).
 */
public class HealAbility implements Ability {

    private final String name;
    private final int power;

    public HealAbility(String name, int power) {
        this.name = Objects.requireNonNull(name, "name");
        if (power <= 0) {
            throw new IllegalArgumentException("power must be > 0");
        }
        this.power = power;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int use(Combatant user, Combatant target) {
        return target.heal(power);
    }
}
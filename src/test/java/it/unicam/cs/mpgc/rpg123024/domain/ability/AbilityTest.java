package it.unicam.cs.mpgc.rpg123024.domain.ability;

import it.unicam.cs.mpgc.rpg123024.domain.effect.ShieldEffect;
import it.unicam.cs.mpgc.rpg123024.domain.model.Combatant;
import it.unicam.cs.mpgc.rpg123024.domain.model.Monster;
import it.unicam.cs.mpgc.rpg123024.domain.model.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AbilityTest {

    private Combatant attacker;
    private Combatant target;

    @BeforeEach
    void setUp() {
        attacker = new Monster("Attacker", new Stats(50, 10, 0, 1), 0);
        target = new Monster("Target", new Stats(50, 5, 4, 1), 0);
    }

    @Test
    void physicalAttackSubtractsDefenseFromAttack() {
        int damage = new PhysicalAttack().use(attacker, target);

        assertEquals(6, damage);
        assertEquals(44, target.currentHealth());
    }

    @Test
    void physicalAttackDealsAtLeastOneDamage() {
        Combatant weak = new Monster("Weak", new Stats(50, 1, 0, 1), 0);
        Combatant armored = new Monster("Armored", new Stats(50, 1, 99, 1), 0);

        assertEquals(1, new PhysicalAttack().use(weak, armored));
    }

    @Test
    void physicalAttackTakesStatusEffectsIntoAccount() {
        Combatant shielded = new ShieldEffect(target, 3, 2);

        int damage = new PhysicalAttack().use(attacker, shielded);

        assertEquals(3, damage);
    }

    @Test
    void healAbilityRestoresHealthUpToMaximum() {
        target.takeDamage(20);

        int healed = new HealAbility("Heal", 30).use(attacker, target);

        assertEquals(20, healed);
        assertEquals(50, target.currentHealth());
    }

    @Test
    void healAbilityRejectsNonPositivePower() {
        assertThrows(IllegalArgumentException.class, () -> new HealAbility("Heal", 0));
    }
}
package it.unicam.cs.mpgc.rpg123024.domain.combat;

import it.unicam.cs.mpgc.rpg123024.domain.ability.Ability;

import java.util.Objects;

/**
 * Azione che esegue una {@link Ability} su sé stessi o sull'avversario.
 * <p>
 * <b>Responsabilità:</b> collegare Command e Strategy: risolve il destinatario in base a
 * {@link Targeting} e delega il calcolo all'abilità.
 * <p>
 * <b>Pattern:</b> Command concreto che compone una Strategy. <b>SOLID:</b> Single Responsibility
 * (solo risoluzione del bersaglio e reporting); Open/Closed (ogni nuova abilità è utilizzabile
 * senza nuove classi di azione).
 */
public class AbilityAction implements TurnAction {

    private final Ability ability;
    private final Targeting targeting;

    public AbilityAction(Ability ability, Targeting targeting) {
        this.ability = Objects.requireNonNull(ability, "ability");
        this.targeting = Objects.requireNonNull(targeting, "targeting");
    }

    @Override
    public ActionResult execute(Fighter actor, Fighter opponent) {
        Fighter recipient = targeting == Targeting.SELF ? actor : opponent;
        int amount = ability.use(actor.combatant(), recipient.combatant());
        return new ActionResult(actor.combatant().name(), ability.name(),
                recipient.combatant().name(), amount);
    }
}
package it.unicam.cs.mpgc.rpg123024.domain.combat;

import it.unicam.cs.mpgc.rpg123024.domain.effect.CooldownEffect;
import it.unicam.cs.mpgc.rpg123024.domain.effect.StatusEffect;
import it.unicam.cs.mpgc.rpg123024.domain.model.Combatant;

import java.util.Objects;

/**
 * Azione con tempo di ricarica: dopo l'uso non è disponibile per un certo numero di turni di chi
 * la esegue.
 * <p>
 * <b>Responsabilità:</b> avvolgere un'altra {@link TurnAction}, eseguirla e poi applicare all'attore
 * un {@link CooldownEffect}; finché l'effetto è attivo {@link #isAvailableFor(Combatant)} restituisce
 * {@code false}. Lo stato della ricarica vive nel combattente decorato, non in questa classe, quindi la
 * stessa istanza è riutilizzabile in più combattimenti e la ricarica non sopravvive allo scontro.
 * <p>
 * <b>Pattern:</b> Decorator applicato al Command (aggiunge un vincolo a qualsiasi azione) e riuso del
 * Decorator degli effetti di stato. <b>SOLID:</b> Open/Closed (nuova regola senza modificare
 * {@link CombatEngine}, abilità o altre azioni); Single Responsibility (solo la ricarica); Liskov
 * (utilizzabile ovunque serva una {@code TurnAction}).
 */
public class CooldownAction implements TurnAction {

    private final String name;
    private final TurnAction action;
    private final int turns;

    public CooldownAction(String name, TurnAction action, int turns) {
        this.name = Objects.requireNonNull(name, "name");
        this.action = Objects.requireNonNull(action, "action");
        if (turns <= 0) {
            throw new IllegalArgumentException("turns must be > 0");
        }
        this.turns = turns;
    }

    @Override
    public ActionResult execute(Fighter actor, Fighter opponent) {
        if (!isAvailableFor(actor.combatant())) {
            throw new IllegalStateException(name + " is still recharging");
        }
        ActionResult result = action.execute(actor, opponent);
        actor.applyEffect(combatant -> new CooldownEffect(combatant, name, turns));
        return result;
    }

    @Override
    public boolean isAvailableFor(Combatant actor) {
        Combatant current = actor;
        while (current instanceof StatusEffect effect) {
            if (effect instanceof CooldownEffect cooldown && !cooldown.isExpired()
                    && cooldown.actionName().equals(name)) {
                return false;
            }
            current = effect.unwrap();
        }
        return true;
    }
}

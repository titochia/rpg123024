package it.unicam.cs.mpgc.rpg123024.domain.combat;

import it.unicam.cs.mpgc.rpg123024.domain.model.Combatant;

import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Azione che applica un effetto di stato (decoratore) a sé stessi o all'avversario.
 * <p>
 * <b>Responsabilità:</b> risolvere il destinatario e chiedere al suo {@link Fighter} di avvolgersi
 * nel nuovo effetto. Completa il pattern Decorator: l'orchestrazione della sostituzione del
 * riferimento avviene qui, non nelle abilità.
 * <p>
 * <b>Pattern:</b> Command concreto. <b>SOLID:</b> Open/Closed (l'effetto è un parametro, ad
 * esempio {@code c -> new PoisonEffect(c, 3, 3)}); Single Responsibility.
 */
public class ApplyEffectAction implements TurnAction {

    private final String name;
    private final UnaryOperator<Combatant> effect;
    private final Targeting targeting;

    public ApplyEffectAction(String name, UnaryOperator<Combatant> effect, Targeting targeting) {
        this.name = Objects.requireNonNull(name, "name");
        this.effect = Objects.requireNonNull(effect, "effect");
        this.targeting = Objects.requireNonNull(targeting, "targeting");
    }

    @Override
    public ActionResult execute(Fighter actor, Fighter opponent) {
        Fighter recipient = targeting == Targeting.SELF ? actor : opponent;
        recipient.applyEffect(effect);
        return new ActionResult(actor.combatant().name(), name, recipient.combatant().name(), 0);
    }
}
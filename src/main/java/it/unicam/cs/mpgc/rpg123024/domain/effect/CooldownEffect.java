package it.unicam.cs.mpgc.rpg123024.domain.effect;

import it.unicam.cs.mpgc.rpg123024.domain.model.Combatant;

import java.util.Objects;

/**
 * Effetto di stato che segnala che un'azione del combattente è in ricarica per un numero di turni.
 * <p>
 * <b>Responsabilità:</b> ricordare quale azione è temporaneamente non disponibile; non modifica
 * statistiche né salute. Riusa il ciclo di vita degli effetti (tick a inizio turno, scadenza,
 * rimozione da parte di {@code Fighter}), quindi la ricarica si azzera automaticamente a fine
 * combattimento, quando il {@code Fighter} viene scartato.
 * <p>
 * <b>Pattern:</b> Decorator concreto (marcatore temporale). <b>SOLID:</b> Single Responsibility (solo
 * la durata della ricarica); Open/Closed (aggiunto senza modificare {@link StatusEffect}, il motore o
 * le entità).
 */
public class CooldownEffect extends StatusEffect {

    private final String actionName;

    public CooldownEffect(Combatant delegate, String actionName, int duration) {
        super(delegate, duration);
        this.actionName = Objects.requireNonNull(actionName, "actionName");
    }

    public String actionName() {
        return actionName;
    }

    @Override
    public String effectName() {
        return actionName + " cooldown";
    }
}

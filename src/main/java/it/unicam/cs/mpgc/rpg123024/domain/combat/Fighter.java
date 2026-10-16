package it.unicam.cs.mpgc.rpg123024.domain.combat;

import it.unicam.cs.mpgc.rpg123024.domain.effect.StatusEffect;
import it.unicam.cs.mpgc.rpg123024.domain.model.Combatant;

import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Partecipante a un combattimento: mantiene la vista corrente (decorata) di un {@link Combatant}.
 * <p>
 * <b>Responsabilità:</b> possedere il riferimento riassegnabile al combattente, applicare nuovi
 * {@link StatusEffect} avvolgendolo, gestire l'inizio turno (tick degli effetti) e rimuovere gli
 * effetti scaduti affioranti come livello più esterno. Risolve il vincolo del Decorator: sostituire
 * il riferimento al bersaglio senza che gli altri componenti ne siano consapevoli.
 * <p>
 * <b>SOLID:</b> Single Responsibility (ciclo di vita degli effetti, non regole di combattimento);
 * Open/Closed (qualsiasi nuovo effetto è un {@code UnaryOperator} applicato, nessuna modifica qui);
 * Dependency Inversion (lavora solo su {@code Combatant}).
 */
public final class Fighter {

    private Combatant current;

    public Fighter(Combatant base) {
        this.current = Objects.requireNonNull(base, "base");
    }

    public Combatant combatant() {
        return current;
    }

    public void applyEffect(UnaryOperator<Combatant> effect) {
        current = Objects.requireNonNull(effect.apply(current), "effect result");
    }

    /**
     * Esegue l'inizio turno: propaga il tick agli effetti attivi e rimuove quelli scaduti.
     *
     * @return salute persa a causa degli effetti periodici
     */
    public int startTurn() {
        int before = current.currentHealth();
        if (current instanceof StatusEffect effect) {
            effect.onTurnStart();
        }
        while (current instanceof StatusEffect effect && effect.isExpired()) {
            current = effect.unwrap();
        }
        return before - current.currentHealth();
    }
}
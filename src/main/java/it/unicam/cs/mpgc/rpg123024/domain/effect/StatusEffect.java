package it.unicam.cs.mpgc.rpg123024.domain.effect;

import it.unicam.cs.mpgc.rpg123024.domain.model.Combatant;
import it.unicam.cs.mpgc.rpg123024.domain.model.Stats;

import java.util.Objects;

/**
 * Decoratore astratto di {@link Combatant} che aggiunge un effetto temporaneo (pattern Decorator).
 * <p>
 * <b>Responsabilità:</b> delegare per default tutto al combattente decorato, gestire la durata
 * residua e propagare il tick di inizio turno agli effetti annidati. Le sottoclassi specializzano
 * solo ciò che cambia: {@link #stats()} (modificatori) e/o {@link #applyTurnEffect()} (effetti
 * periodici).
 * <p>
 * <b>SOLID:</b> Open/Closed (nuovi effetti senza toccare {@code Hero}, {@code Monster} o il motore);
 * Liskov (un effetto è sostituibile ovunque serva un {@code Combatant}); Single Responsibility
 * (durata e delega qui, comportamento specifico nelle sottoclassi). Gli effetti scaduti sono inerti;
 * il motore rimuove il livello più esterno tramite {@link #unwrap()}.
 */
public abstract class StatusEffect implements Combatant {

    private final Combatant delegate;
    private int remainingTurns;

    protected StatusEffect(Combatant delegate, int duration) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        if (duration <= 0) {
            throw new IllegalArgumentException("duration must be > 0");
        }
        this.remainingTurns = duration;
    }

    public abstract String effectName();

    public final void onTurnStart() {
        if (delegate instanceof StatusEffect inner) {
            inner.onTurnStart();
        }
        if (!isExpired()) {
            applyTurnEffect();
            remainingTurns--;
        }
    }

    protected void applyTurnEffect() {
    }

    public boolean isExpired() {
        return remainingTurns <= 0;
    }

    public int remainingTurns() {
        return remainingTurns;
    }

    public Combatant unwrap() {
        return delegate;
    }

    protected Combatant delegate() {
        return delegate;
    }

    @Override
    public String name() {
        return delegate.name();
    }

    @Override
    public Stats stats() {
        return delegate.stats();
    }

    @Override
    public int currentHealth() {
        return delegate.currentHealth();
    }

    @Override
    public int takeDamage(int amount) {
        return delegate.takeDamage(amount);
    }

    @Override
    public int heal(int amount) {
        return delegate.heal(amount);
    }
}
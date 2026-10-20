package it.unicam.cs.mpgc.rpg123024.domain.model;

import java.util.Objects;

/**
 * Implementazione base di {@link Combatant} che gestisce nome, statistiche base e salute.
 *
 * <p>
 * <b>Responsabilità:</b> centralizzare la logica comune di danno e cura (clamping dei valori)
 * ed evitare duplicazioni tra {@link Hero} e {@link Monster} (DRY). Non aggiunge comportamenti
 * specifici di una categoria di entità.
 *
 * <p>
 * <b>Architettura:</b> classe di supporto del Domain, non esposta oltre il package model
 * se non tramite le sue sottoclassi.
 */
public abstract class AbstractCombatant implements Combatant {

    private final String name;
    private Stats baseStats;
    private int currentHealth;

    /**
     * @param name      nome non vuoto
     * @param baseStats statistiche iniziali; la salute parte dal massimo
     */
    protected AbstractCombatant(String name, Stats baseStats) {
        this(name, baseStats, Objects.requireNonNull(baseStats, "baseStats").maxHealth());
    }

    protected AbstractCombatant(String name, Stats baseStats, int currentHealth) {
        Objects.requireNonNull(name, "name");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        this.name = name;
        this.baseStats = Objects.requireNonNull(baseStats, "baseStats");
        if (currentHealth < 0 || currentHealth > baseStats.maxHealth()) {
            throw new IllegalArgumentException("currentHealth out of range");
        }
        this.currentHealth = currentHealth;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Stats stats() {
        return baseStats;
    }

    @Override
    public int currentHealth() {
        return currentHealth;
    }

    @Override
    public int takeDamage(int amount) {
        requireNonNegative(amount);
        int applied = Math.min(amount, currentHealth);
        currentHealth -= applied;
        return applied;
    }

    @Override
    public int heal(int amount) {
        requireNonNegative(amount);
        int applied = Math.min(amount, stats().maxHealth() - currentHealth);
        currentHealth += applied;
        return applied;
    }

    /**
     * Incrementa in modo permanente le statistiche base (es. avanzamento di livello);
     * l'aumento di salute massima si riflette anche sulla salute corrente.
     *
     * @param growth incremento da sommare alle statistiche base
     */
    protected void growBaseStats(Stats growth) {
        baseStats = baseStats.plus(growth);
        currentHealth += growth.maxHealth();
    }

    private static void requireNonNegative(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be >= 0");
        }
    }
}
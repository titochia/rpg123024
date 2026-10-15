package it.unicam.cs.mpgc.rpg123024.domain.model;

/**
 * Contratto di ogni entità che può partecipare a un combattimento.
 * <p>
 * <b>Responsabilità:</b> esporre nome, statistiche effettive e salute corrente, e consentire
 * di subire danni o di essere curata. Non conosce le regole di combattimento (calcolo del danno,
 * ordine dei turni), che appartengono al motore di combattimento.
 * <p>
 * <b>Architettura:</b> è l'astrazione su cui dipendono abilità, effetti di stato e motore di
 * combattimento (Dependency Inversion). Essendo un'interfaccia, consente di applicare il pattern
 * Decorator (effetti di stato che modificano {@link #stats()}) senza toccare {@code Hero} e
 * {@code Monster} (Open/Closed).
 */
public interface Combatant {

    /** @return nome visualizzabile dell'entità */
    String name();

    /** @return statistiche effettive correnti (comprensive di eventuali modificatori) */
    Stats stats();

    /** @return punti salute correnti, sempre compresi tra 0 e {@code stats().maxHealth()} */
    int currentHealth();

    /**
     * Sottrae salute, senza scendere sotto zero.
     *
     * @param amount danno da applicare (non negativo)
     * @return danno effettivamente applicato
     */
    int takeDamage(int amount);

    /**
     * Ripristina salute, senza superare il massimo.
     *
     * @param amount quantità da curare (non negativa)
     * @return salute effettivamente recuperata
     */
    int heal(int amount);

    /** @return {@code true} se la salute corrente è maggiore di zero */
    default boolean isAlive() {
        return currentHealth() > 0;
    }
}
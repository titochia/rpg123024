package it.unicam.cs.mpgc.rpg123024.domain.model;

/**
 * Value object immutabile che rappresenta le statistiche di combattimento di un {@link Combatant}.
 * <p>
 * <b>Responsabilità:</b> incapsulare i valori numerici (salute massima, attacco, difesa, velocità),
 * garantirne la validità (invarianti) e permetterne la composizione tramite {@link #plus(Stats)}.
 * Non contiene stato mutabile né logica di combattimento.
 * <p>
 * <b>Architettura:</b> appartiene al layer Domain; è usata da {@code Combatant}, dalle abilità
 * e dal motore di combattimento come unità di misura condivisa.
 *
 * @param maxHealth salute massima (strettamente positiva)
 * @param attack    potenza offensiva (non negativa)
 * @param defense   resistenza ai danni (non negativa)
 * @param speed     velocità, determina l'ordine dei turni (non negativa)
 */
public record Stats(int maxHealth, int attack, int defense, int speed) {

    public Stats {
        if (maxHealth <= 0) {
            throw new IllegalArgumentException("maxHealth must be > 0");
        }
        if (attack < 0 || defense < 0 || speed < 0) {
            throw new IllegalArgumentException("attack, defense and speed must be >= 0");
        }
    }

    /**
     * Restituisce una nuova istanza ottenuta sommando campo per campo le statistiche fornite.
     *
     * @param other statistiche da sommare
     * @return nuove statistiche risultanti
     */
    public Stats plus(Stats other) {
        return new Stats(
                maxHealth + other.maxHealth,
                attack + other.attack,
                defense + other.defense,
                speed + other.speed);
    }
}
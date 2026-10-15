package it.unicam.cs.mpgc.rpg123024.domain.model;

/**
 * Nemico affrontato dall'eroe.
 * <p>
 * <b>Responsabilità:</b> estendere {@link AbstractCombatant} con la ricompensa di esperienza
 * assegnata a chi lo sconfigge. Il comportamento in battaglia (scelta delle azioni) non è qui:
 * sarà delegato a una strategia di intelligenza artificiale separata.
 * <p>
 * <b>Architettura:</b> entità del Domain, istanziata dalle factory dei mostri.
 */
public class Monster extends AbstractCombatant {

    private final int experienceReward;

    /**
     * @param name             nome del mostro
     * @param baseStats        statistiche del mostro
     * @param experienceReward esperienza assegnata alla sconfitta (non negativa)
     */
    public Monster(String name, Stats baseStats, int experienceReward) {
        super(name, baseStats);
        if (experienceReward < 0) {
            throw new IllegalArgumentException("experienceReward must be >= 0");
        }
        this.experienceReward = experienceReward;
    }

    /** @return esperienza assegnata a chi sconfigge il mostro */
    public int experienceReward() {
        return experienceReward;
    }
}
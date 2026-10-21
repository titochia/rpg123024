package it.unicam.cs.mpgc.rpg123024.gui.view;

/**
 * View model immutabile con i dati dell'eroe da visualizzare (progressione, salute, statistiche).
 * <p>
 * <b>Responsabilità:</b> trasportare verso la View solo dati già pronti, così che la View non
 * dipenda dal Domain né debba calcolare nulla.
 * <p>
 * <b>Pattern:</b> Value Object / View Model (MVP). <b>SOLID:</b> Single Responsibility (solo forma
 * dei dati per la presentazione).
 */
public record HeroStatus(String name, int level, int experience, int experienceToNextLevel,
                         int health, int maxHealth, int attack, int defense, int speed) {
}

package it.unicam.cs.mpgc.rpg123024.persistence.dto;

import java.util.List;

/**
 * Rappresentazione serializzabile (DTO) dello stato salvato di una partita.
 * <p>
 * <b>Responsabilità:</b> descrivere la struttura del file di salvataggio con soli dati primitivi,
 * stringhe e liste (gli oggetti sono riferiti per {@code id}), con un numero di versione per
 * l'evoluzione del formato. Non contiene logica.
 * <p>
 * <b>Architettura:</b> confine di persistenza: isola il formato su disco dal modello di dominio,
 * che può evolvere senza invalidare automaticamente i salvataggi (e viceversa).
 * <b>SOLID:</b> Single Responsibility (solo forma dei dati).
 */
public record GameSnapshot(int version, HeroDto hero, DungeonDto dungeon, PositionDto position) {

    public record PositionDto(int row, int col) { }

    public record StatsDto(int maxHealth, int attack, int defense, int speed) { }

    public record HeroDto(String name, StatsDto baseStats, int currentHealth, int level,
                          int experience, int inventoryCapacity, List<String> items) { }

    public record MonsterDto(String name, StatsDto stats, int currentHealth, int experienceReward) { }

    public record RoomDto(int row, int col, boolean visited, MonsterDto monster, List<String> loot) { }

    public record DungeonDto(int rows, int cols, PositionDto start, PositionDto exit, List<RoomDto> rooms) { }
}
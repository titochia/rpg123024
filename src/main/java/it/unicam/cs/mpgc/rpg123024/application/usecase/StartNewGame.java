package it.unicam.cs.mpgc.rpg123024.application.usecase;

import it.unicam.cs.mpgc.rpg123024.application.state.GameSession;
import it.unicam.cs.mpgc.rpg123024.domain.combat.MonsterAI;
import it.unicam.cs.mpgc.rpg123024.domain.map.DungeonGenerator;
import it.unicam.cs.mpgc.rpg123024.domain.model.Hero;
import it.unicam.cs.mpgc.rpg123024.domain.model.Stats;

import java.util.Objects;

/**
 * Use case: avvio di una nuova partita.
 * <p>
 * <b>Responsabilità:</b> creare l'eroe, generare il dungeon e comporre la {@link GameSession}
 * iniziale. Non decide come il dungeon è generato né come l'AI sceglie le azioni: riceve queste
 * strategie.
 * <p>
 * <b>SOLID:</b> Single Responsibility (un solo caso d'uso); Dependency Inversion (dipende da
 * {@code DungeonGenerator} e {@code MonsterAI}, non da implementazioni).
 */
public class StartNewGame {

    private final DungeonGenerator generator;
    private final MonsterAI monsterAI;
    private final Stats initialHeroStats;

    public StartNewGame(DungeonGenerator generator, MonsterAI monsterAI, Stats initialHeroStats) {
        this.generator = Objects.requireNonNull(generator, "generator");
        this.monsterAI = Objects.requireNonNull(monsterAI, "monsterAI");
        this.initialHeroStats = Objects.requireNonNull(initialHeroStats, "initialHeroStats");
    }

    public GameSession execute(String heroName) {
        return new GameSession(new Hero(heroName, initialHeroStats), generator.generate(), monsterAI);
    }
}
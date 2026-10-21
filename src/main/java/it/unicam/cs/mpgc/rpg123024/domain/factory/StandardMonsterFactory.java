package it.unicam.cs.mpgc.rpg123024.domain.factory;

import it.unicam.cs.mpgc.rpg123024.domain.model.Monster;
import it.unicam.cs.mpgc.rpg123024.domain.model.Stats;
import it.unicam.cs.mpgc.rpg123024.domain.random.RandomSource;

import java.util.List;

/**
 * Catalogo predefinito dei mostri del gioco, scelti a caso a ogni creazione.
 * <p>
 * <b>Responsabilità:</b> definire in un solo punto i tipi di mostro (nome, statistiche, esperienza)
 * e crearne un'istanza nuova a ogni richiesta, poiché i mostri hanno stato mutabile (salute).
 * <p>
 * <b>Pattern:</b> Factory + Registry di modelli (ogni {@code Template} descrive come costruire un
 * mostro). <b>SOLID:</b> Single Responsibility (solo contenuto e creazione dei mostri); Open/Closed
 * (nuovi mostri aggiungendo un modello, senza toccare generatore o combattimento); Dependency
 * Inversion (la casualità è iniettata tramite {@link RandomSource}).
 */
public class StandardMonsterFactory implements MonsterFactory {

    private record Template(String name, Stats stats, int experienceReward) {

        Monster create() {
            return new Monster(name, stats, experienceReward);
        }
    }

    private static final List<Template> TEMPLATES = List.of(
            new Template("Goblin", new Stats(25, 6, 1, 6), 25),
            new Template("Skeleton", new Stats(35, 8, 2, 4), 35),
            new Template("Orc", new Stats(50, 10, 3, 3), 50),
            new Template("Troll", new Stats(70, 12, 4, 2), 80));

    @Override
    public Monster create(RandomSource random) {
        return TEMPLATES.get(random.nextInt(TEMPLATES.size())).create();
    }
}

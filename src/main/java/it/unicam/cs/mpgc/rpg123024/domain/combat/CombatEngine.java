package it.unicam.cs.mpgc.rpg123024.domain.combat;

import it.unicam.cs.mpgc.rpg123024.domain.model.Combatant;
import it.unicam.cs.mpgc.rpg123024.domain.model.Hero;
import it.unicam.cs.mpgc.rpg123024.domain.model.Monster;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Motore di un combattimento a turni alternati tra un {@link Hero} e un {@link Monster}.
 * <p>
 * <b>Responsabilità:</b> stabilire chi inizia (velocità maggiore, parità a favore dell'eroe),
 * far rispettare l'alternanza, eseguire l'inizio turno (effetti periodici), invocare la
 * {@link TurnAction} scelta e determinare l'esito. Non conosce GUI, input né persistenza; non assegna
 * esperienza né bottino (compito degli use case).
 * <p>
 * <b>Pattern:</b> orchestrazione di Command (azioni), Strategy ({@link MonsterAI}) e Decorator
 * (tramite {@link Fighter}). <b>SOLID:</b> Single Responsibility (flusso del combattimento);
 * Open/Closed e Dependency Inversion (azioni e AI sono astrazioni iniettate).
 */
public class CombatEngine {

    /** Stato del combattimento. */
    public enum Outcome { ONGOING, HERO_WON, HERO_DEFEATED }

    private final Fighter hero;
    private final Fighter monster;
    private final MonsterAI ai;
    private boolean heroTurn;

    public CombatEngine(Hero hero, Monster monster, MonsterAI ai) {
        Objects.requireNonNull(hero, "hero");
        Objects.requireNonNull(monster, "monster");
        this.ai = Objects.requireNonNull(ai, "ai");
        this.hero = new Fighter(hero);
        this.monster = new Fighter(monster);
        this.heroTurn = hero.stats().speed() >= monster.stats().speed();
    }

    public boolean isHeroTurn() {
        return heroTurn;
    }

    public Outcome outcome() {
        if (!monster.combatant().isAlive()) {
            return Outcome.HERO_WON;
        }
        if (!hero.combatant().isAlive()) {
            return Outcome.HERO_DEFEATED;
        }
        return Outcome.ONGOING;
    }

    public boolean isOver() {
        return outcome() != Outcome.ONGOING;
    }

    public Combatant heroCombatant() {
        return hero.combatant();
    }

    public Combatant monsterCombatant() {
        return monster.combatant();
    }

    public List<ActionResult> heroAct(TurnAction action) {
        Objects.requireNonNull(action, "action");
        requireTurn(true);
        return act(hero, monster, () -> action);
    }

    public List<ActionResult> monsterAct() {
        requireTurn(false);
        return act(monster, hero, () -> ai.chooseAction(monster, hero));
    }

    private List<ActionResult> act(Fighter actor, Fighter opponent, Supplier<TurnAction> choice) {
        List<ActionResult> results = new ArrayList<>();
        int lostToEffects = actor.startTurn();
        if (lostToEffects > 0) {
            String name = actor.combatant().name();
            results.add(new ActionResult(name, "Status effects", name, lostToEffects));
        }
        if (!isOver()) {
            results.add(choice.get().execute(actor, opponent));
        }
        heroTurn = !heroTurn;
        return results;
    }

    private void requireTurn(boolean expectedHeroTurn) {
        if (isOver()) {
            throw new IllegalStateException("combat is over");
        }
        if (heroTurn != expectedHeroTurn) {
            throw new IllegalStateException(expectedHeroTurn ? "not the hero's turn" : "not the monster's turn");
        }
    }
}
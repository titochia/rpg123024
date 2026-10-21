package it.unicam.cs.mpgc.rpg123024.gui.presenter;

import it.unicam.cs.mpgc.rpg123024.domain.effect.StatusEffect;
import it.unicam.cs.mpgc.rpg123024.domain.model.Combatant;
import it.unicam.cs.mpgc.rpg123024.domain.model.Hero;
import it.unicam.cs.mpgc.rpg123024.domain.model.Stats;
import it.unicam.cs.mpgc.rpg123024.gui.view.CombatantStatus;
import it.unicam.cs.mpgc.rpg123024.gui.view.HeroStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Conversioni dalle entità di dominio ai view model usati dalle View.
 * <p>
 * <b>Responsabilità:</b> costruire {@link HeroStatus} e {@link CombatantStatus}, risalendo la catena
 * di {@link StatusEffect} per elencare gli effetti attivi. Centralizza la traduzione per tutti i
 * Presenter.
 * <p>
 * <b>SOLID:</b> Single Responsibility (solo mappatura verso la presentazione); DRY (riusata da più
 * Presenter); la View non dipende mai dal Domain.
 */
final class ViewModels {

    private ViewModels() {
    }

    static HeroStatus heroStatus(Hero hero) {
        Stats stats = hero.stats();
        return new HeroStatus(hero.name(), hero.level(), hero.experience(), hero.experienceToNextLevel(),
                hero.currentHealth(), stats.maxHealth(), stats.attack(), stats.defense(), stats.speed());
    }

    static CombatantStatus combatantStatus(Combatant combatant) {
        Stats stats = combatant.stats();
        return new CombatantStatus(combatant.name(), combatant.currentHealth(), stats.maxHealth(),
                stats.attack(), stats.defense(), stats.speed(), activeEffects(combatant));
    }

    private static List<String> activeEffects(Combatant combatant) {
        List<String> effects = new ArrayList<>();
        Combatant current = combatant;
        while (current instanceof StatusEffect effect) {
            if (!effect.isExpired()) {
                effects.add(effect.effectName() + " (" + effect.remainingTurns() + ")");
            }
            current = effect.unwrap();
        }
        return effects;
    }
}

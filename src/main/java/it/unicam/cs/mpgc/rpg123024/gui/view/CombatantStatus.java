package it.unicam.cs.mpgc.rpg123024.gui.view;

import java.util.List;

/**
 * View model immutabile di un partecipante al combattimento, con statistiche effettive (decorate)
 * ed effetti di stato attivi.
 * <p>
 * <b>Responsabilità:</b> fornire alla View i dati del combattente già risolti, inclusi i nomi
 * degli effetti in corso, senza esporre la catena di decoratori del Domain.
 * <p>
 * <b>Pattern:</b> Value Object / View Model (MVP). <b>SOLID:</b> Single Responsibility.
 */
public record CombatantStatus(String name, int health, int maxHealth, int attack, int defense,
                              int speed, List<String> effects) {

    public CombatantStatus {
        effects = List.copyOf(effects);
    }
}

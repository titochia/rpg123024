package it.unicam.cs.mpgc.rpg123024.domain.combat;

import it.unicam.cs.mpgc.rpg123024.domain.model.Hero;
import it.unicam.cs.mpgc.rpg123024.domain.model.Item;

import java.util.Objects;

/**
 * Azione che consuma un {@link Item} dall'inventario dell'eroe applicandone l'effetto.
 * <p>
 * <b>Responsabilità:</b> invocare {@code Hero.useItem} e riportare la variazione di salute
 * risultante. Il ricevitore (eroe e oggetto) è legato in costruzione, come da definizione classica
 * del Command; i {@link Fighter} ricevuti in esecuzione non sono necessari.
 * <p>
 * <b>Pattern:</b> Command concreto. <b>SOLID:</b> Single Responsibility; Open/Closed (qualunque
 * oggetto, essendo uno {@code Item.Effect}, funziona senza modifiche).
 */
public class UseItemAction implements TurnAction {

    private final Hero hero;
    private final Item item;

    public UseItemAction(Hero hero, Item item) {
        this.hero = Objects.requireNonNull(hero, "hero");
        this.item = Objects.requireNonNull(item, "item");
    }

    @Override
    public ActionResult execute(Fighter actor, Fighter opponent) {
        int before = hero.currentHealth();
        if (!hero.useItem(item)) {
            throw new IllegalStateException("item not in inventory: " + item.id());
        }
        return new ActionResult(hero.name(), item.name(), hero.name(), hero.currentHealth() - before);
    }
}
package it.unicam.cs.mpgc.rpg123024.domain.factory;

import it.unicam.cs.mpgc.rpg123024.domain.model.Item;
import it.unicam.cs.mpgc.rpg123024.domain.random.RandomSource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Catalogo predefinito degli oggetti di gioco, usato sia per la ricostruzione ({@link ItemCatalog})
 * sia per la generazione casuale del bottino ({@link ItemFactory}).
 * <p>
 * <b>Responsabilità:</b> definire in un solo punto gli oggetti esistenti (id, nome, effetto) e
 * fornirli per id o a caso. Aggiungere un oggetto significa registrarne uno nuovo.
 * <p>
 * <b>Pattern:</b> Factory + Registry. <b>SOLID:</b> Single Responsibility (contenuto degli
 * oggetti); Open/Closed (estendibile per registrazione); una sola classe soddisfa due astrazioni
 * (Interface Segregation).
 */
public class StandardItemCatalog implements ItemCatalog, ItemFactory {

    private final Map<String, Item> items = new LinkedHashMap<>();

    public StandardItemCatalog() {
        register(new Item("small-potion", "Small Potion", "Restores 30 HP", target -> target.heal(30)));
        register(new Item("large-potion", "Large Potion", "Restores 60 HP", target -> target.heal(60)));
    }

    private void register(Item item) {
        items.put(item.id(), item);
    }

    @Override
    public Optional<Item> findById(String id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Item create(RandomSource random) {
        List<Item> all = List.copyOf(items.values());
        return all.get(random.nextInt(all.size()));
    }
}
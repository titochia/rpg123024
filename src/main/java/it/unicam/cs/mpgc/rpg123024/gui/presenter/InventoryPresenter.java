package it.unicam.cs.mpgc.rpg123024.gui.presenter;

import it.unicam.cs.mpgc.rpg123024.application.state.GameSession;
import it.unicam.cs.mpgc.rpg123024.application.usecase.UseItem;
import it.unicam.cs.mpgc.rpg123024.domain.model.Hero;
import it.unicam.cs.mpgc.rpg123024.domain.model.Item;
import it.unicam.cs.mpgc.rpg123024.gui.view.InventoryView;
import it.unicam.cs.mpgc.rpg123024.gui.view.ItemEntry;

import java.util.List;
import java.util.Objects;

/**
 * Presenter della schermata dell'inventario, condivisa da esplorazione e combattimento.
 * <p>
 * <b>Responsabilità:</b> elencare gli oggetti dell'eroe, usare quello scelto tramite {@link UseItem}
 * e decidere dove tornare: in esplorazione l'inventario resta aperto, in combattimento l'uso consuma
 * il turno e si torna allo scontro (o all'esito). La differenza di regole è già nelle fasi: qui si
 * legge solo la fase risultante.
 * <p>
 * <b>Pattern:</b> MVP (Presenter). <b>SOLID:</b> Single Responsibility; Dependency Inversion;
 * riuso della stessa schermata in due contesti senza condizionali sulle regole (DRY).
 */
public class InventoryPresenter {

    private final InventoryView view;
    private final UseItem useItem;
    private final GameSessionHolder sessions;
    private final Navigator navigator;

    public InventoryPresenter(InventoryView view, UseItem useItem, GameSessionHolder sessions,
                              Navigator navigator) {
        this.view = Objects.requireNonNull(view, "view");
        this.useItem = Objects.requireNonNull(useItem, "useItem");
        this.sessions = Objects.requireNonNull(sessions, "sessions");
        this.navigator = Objects.requireNonNull(navigator, "navigator");
    }

    public void refresh() {
        Hero hero = sessions.require().hero();
        List<ItemEntry> entries = hero.inventory().items().stream()
                .map(item -> new ItemEntry(item.name(), item.description()))
                .toList();
        view.showItems(entries, hero.inventory().capacity());
        view.showHero(ViewModels.heroStatus(hero));
    }

    public void onUse(int index) {
        GameSession session = sessions.require();
        boolean done = SafeCommand.run(view, () -> {
            List<Item> items = session.hero().inventory().items();
            if (index < 0 || index >= items.size()) {
                throw new IllegalArgumentException("no item selected");
            }
            useItem.execute(session, items.get(index));
        });
        if (!done) {
            return;
        }
        sessions.markChanged();
        if ("EXPLORING".equals(session.phase().id())) {
            refresh();
        } else {
            navigator.showPhase(session.phase());
        }
    }

    public void onBack() {
        navigator.showPhase(sessions.require().phase());
    }
}

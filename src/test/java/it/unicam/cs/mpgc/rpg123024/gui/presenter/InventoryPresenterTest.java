package it.unicam.cs.mpgc.rpg123024.gui.presenter;

import it.unicam.cs.mpgc.rpg123024.application.Fixtures;
import it.unicam.cs.mpgc.rpg123024.application.state.GameSession;
import it.unicam.cs.mpgc.rpg123024.application.usecase.MoveToRoom;
import it.unicam.cs.mpgc.rpg123024.application.usecase.UseItem;
import it.unicam.cs.mpgc.rpg123024.domain.map.Direction;
import it.unicam.cs.mpgc.rpg123024.domain.map.Dungeon;
import it.unicam.cs.mpgc.rpg123024.domain.map.Position;
import it.unicam.cs.mpgc.rpg123024.domain.model.Hero;
import it.unicam.cs.mpgc.rpg123024.gui.view.ItemEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InventoryPresenterTest {

    private final Fakes.FakeInventoryView view = new Fakes.FakeInventoryView();
    private final Fakes.FakeNavigator navigator = new Fakes.FakeNavigator();
    private final GameSessionHolder sessions = new GameSessionHolder();
    private final GameLog log = new GameLog();
    private final InventoryPresenter presenter =
            new InventoryPresenter(view, new UseItem(log::accept), sessions, navigator);
    private Hero hero;

    @BeforeEach
    void setUp() {
        hero = Fixtures.hero();
        hero.inventory().add(Fixtures.potion());
        hero.takeDamage(50);
    }

    private GameSession session(boolean inCombat) {
        Dungeon dungeon = Fixtures.dungeon();
        dungeon.room(new Position(0, 1)).placeMonster(Fixtures.monster(100, 1, 1, 10));
        GameSession session = Fixtures.session(hero, dungeon);
        if (inCombat) {
            new MoveToRoom(log::accept).execute(session, Direction.EAST);
        }
        sessions.set(session);
        return session;
    }

    @Test
    void refreshListsItemsAndHero() {
        session(false);

        presenter.refresh();

        assertEquals(List.of(new ItemEntry("Potion", "Heals 30 HP")), view.items);
        assertEquals(10, view.capacity);
        assertEquals(50, view.hero.health());
    }

    @Test
    void usingItemWhileExploringKeepsInventoryOpen() {
        session(false);

        presenter.onUse(0);

        assertEquals(80, hero.currentHealth());
        assertTrue(view.items.isEmpty());
        assertTrue(navigator.screens.isEmpty());
    }

    @Test
    void usingItemInCombatReturnsToCombat() {
        session(true);

        presenter.onUse(0);

        assertTrue(hero.inventory().items().isEmpty());
        assertEquals("COMBAT", navigator.last());
    }

    @Test
    void invalidIndexShowsError() {
        session(false);

        presenter.onUse(3);

        assertEquals(1, view.errors.size());
        assertEquals(1, hero.inventory().size());
    }

    @Test
    void backReturnsToCurrentPhase() {
        session(true);

        presenter.onBack();

        assertEquals("COMBAT", navigator.last());
    }
}

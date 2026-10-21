package it.unicam.cs.mpgc.rpg123024.gui.presenter;

import it.unicam.cs.mpgc.rpg123024.application.Fixtures;
import it.unicam.cs.mpgc.rpg123024.application.state.GameSession;
import it.unicam.cs.mpgc.rpg123024.application.usecase.ExecuteTurn;
import it.unicam.cs.mpgc.rpg123024.application.usecase.MoveToRoom;
import it.unicam.cs.mpgc.rpg123024.domain.combat.ApplyEffectAction;
import it.unicam.cs.mpgc.rpg123024.domain.combat.Targeting;
import it.unicam.cs.mpgc.rpg123024.domain.effect.ShieldEffect;
import it.unicam.cs.mpgc.rpg123024.domain.map.Direction;
import it.unicam.cs.mpgc.rpg123024.domain.map.Dungeon;
import it.unicam.cs.mpgc.rpg123024.domain.map.Position;
import it.unicam.cs.mpgc.rpg123024.domain.model.Monster;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatPresenterTest {

    private static final List<NamedAction> ACTIONS = List.of(
            new NamedAction("Attack", Fixtures.ATTACK),
            new NamedAction("Shield", new ApplyEffectAction("Shield", c -> new ShieldEffect(c, 5, 3), Targeting.SELF)));

    private final Fakes.FakeCombatView view = new Fakes.FakeCombatView();
    private final Fakes.FakeNavigator navigator = new Fakes.FakeNavigator();
    private final GameSessionHolder sessions = new GameSessionHolder();
    private final GameLog log = new GameLog();
    private final CombatPresenter presenter =
            new CombatPresenter(view, new ExecuteTurn(log::accept), sessions, log, navigator, ACTIONS);

    private GameSession enterCombatWith(Monster monster) {
        Dungeon dungeon = Fixtures.dungeon();
        dungeon.room(new Position(0, 1)).placeMonster(monster);
        GameSession session = Fixtures.session(Fixtures.hero(), dungeon);
        new MoveToRoom(log::accept).execute(session, Direction.EAST);
        sessions.set(session);
        return session;
    }

    @Test
    void refreshShowsCombatantsActionsAndLog() {
        enterCombatWith(Fixtures.monster(100, 1, 1, 10));

        presenter.refresh();

        assertEquals("Hero", view.hero.name());
        assertEquals("Orc", view.monster.name());
        assertEquals(List.of("Attack", "Shield"), view.actions);
        assertTrue(view.log.contains("A Orc attacks!"));
    }

    @Test
    void refreshShowsActiveEffectsAndDecoratedStats() {
        enterCombatWith(Fixtures.monster(100, 1, 1, 10));

        presenter.onAction(1);
        presenter.refresh();

        assertEquals(7, view.hero.defense());
        assertEquals(1, view.hero.effects().size());
        assertTrue(view.hero.effects().get(0).startsWith("Shield"));
    }

    @Test
    void actionThatContinuesCombatStaysInCombat() {
        enterCombatWith(Fixtures.monster(100, 1, 1, 10));

        presenter.onAction(0);

        assertEquals("COMBAT", navigator.last());
    }

    @Test
    void winningActionNavigatesToExploration() {
        GameSession session = enterCombatWith(Fixtures.monster(9, 1, 1, 10));

        presenter.onAction(0);

        assertEquals("EXPLORATION", navigator.last());
        assertTrue(session.combat().isEmpty());
    }

    @Test
    void invalidActionIndexShowsError() {
        enterCombatWith(Fixtures.monster(100, 1, 1, 10));

        presenter.onAction(5);

        assertEquals(1, view.errors.size());
        assertTrue(navigator.screens.isEmpty());
    }

    @Test
    void refreshWithoutCombatNavigatesToCurrentPhase() {
        sessions.set(Fixtures.session(Fixtures.hero(), Fixtures.dungeon()));

        presenter.refresh();

        assertEquals("EXPLORATION", navigator.last());
    }

    @Test
    void emptyActionListIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new CombatPresenter(view, new ExecuteTurn(log::accept), sessions, log, navigator, List.of()));
    }
}

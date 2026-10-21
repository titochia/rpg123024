package it.unicam.cs.mpgc.rpg123024.gui.presenter;

import it.unicam.cs.mpgc.rpg123024.application.state.GameSession;
import it.unicam.cs.mpgc.rpg123024.application.usecase.ExecuteTurn;
import it.unicam.cs.mpgc.rpg123024.domain.combat.CombatEngine;
import it.unicam.cs.mpgc.rpg123024.gui.view.CombatView;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Presenter della schermata di combattimento.
 * <p>
 * <b>Responsabilità:</b> mostrare lo stato effettivo (decorato) dei contendenti, l'elenco delle
 * azioni dell'eroe e il registro; eseguire l'azione scelta tramite {@link ExecuteTurn} e navigare
 * alla schermata della fase risultante (esplorazione, vittoria o sconfitta).
 * <p>
 * <b>Pattern:</b> MVP (Presenter); le azioni sono Command ({@link NamedAction}) iniettati.
 * <b>SOLID:</b> Single Responsibility; Open/Closed (nuove azioni dalla composition root, senza
 * modifiche qui); Dependency Inversion (View e Navigator sono interfacce).
 */
public class CombatPresenter {

    private final CombatView view;
    private final ExecuteTurn executeTurn;
    private final GameSessionHolder sessions;
    private final GameLog log;
    private final Navigator navigator;
    private final List<NamedAction> actions;

    public CombatPresenter(CombatView view, ExecuteTurn executeTurn, GameSessionHolder sessions,
                           GameLog log, Navigator navigator, List<NamedAction> actions) {
        this.view = Objects.requireNonNull(view, "view");
        this.executeTurn = Objects.requireNonNull(executeTurn, "executeTurn");
        this.sessions = Objects.requireNonNull(sessions, "sessions");
        this.log = Objects.requireNonNull(log, "log");
        this.navigator = Objects.requireNonNull(navigator, "navigator");
        this.actions = List.copyOf(actions);
        if (this.actions.isEmpty()) {
            throw new IllegalArgumentException("at least one action is required");
        }
    }

    public void refresh() {
        GameSession session = sessions.require();
        Optional<CombatEngine> combat = session.combat();
        if (combat.isEmpty()) {
            navigator.showPhase(session.phase());
            return;
        }
        view.showCombatants(ViewModels.combatantStatus(combat.get().heroCombatant()),
                ViewModels.combatantStatus(combat.get().monsterCombatant()));
        view.showActions(actions.stream().map(NamedAction::name).toList());
        view.showLog(log.messages());
    }

    public void onAction(int index) {
        GameSession session = sessions.require();
        boolean done = SafeCommand.run(view, () -> {
            if (index < 0 || index >= actions.size()) {
                throw new IllegalArgumentException("unknown action");
            }
            executeTurn.execute(session, actions.get(index).action());
        });
        if (done) {
            navigator.showPhase(session.phase());
        }
    }

    public void onOpenInventory() {
        navigator.showInventory();
    }
}

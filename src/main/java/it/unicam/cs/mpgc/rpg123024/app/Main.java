package it.unicam.cs.mpgc.rpg123024.app;

import it.unicam.cs.mpgc.rpg123024.application.port.GameRepository;
import it.unicam.cs.mpgc.rpg123024.application.usecase.ExecuteTurn;
import it.unicam.cs.mpgc.rpg123024.application.usecase.LoadGame;
import it.unicam.cs.mpgc.rpg123024.application.usecase.MoveToRoom;
import it.unicam.cs.mpgc.rpg123024.application.usecase.SaveGame;
import it.unicam.cs.mpgc.rpg123024.application.usecase.StartNewGame;
import it.unicam.cs.mpgc.rpg123024.application.usecase.UseItem;
import it.unicam.cs.mpgc.rpg123024.domain.ability.HealAbility;
import it.unicam.cs.mpgc.rpg123024.domain.ability.PhysicalAttack;
import it.unicam.cs.mpgc.rpg123024.domain.combat.AbilityAction;
import it.unicam.cs.mpgc.rpg123024.domain.combat.AggressiveMonsterAI;
import it.unicam.cs.mpgc.rpg123024.domain.combat.ApplyEffectAction;
import it.unicam.cs.mpgc.rpg123024.domain.combat.CooldownAction;
import it.unicam.cs.mpgc.rpg123024.domain.combat.MonsterAI;
import it.unicam.cs.mpgc.rpg123024.domain.combat.Targeting;
import it.unicam.cs.mpgc.rpg123024.domain.effect.PoisonEffect;
import it.unicam.cs.mpgc.rpg123024.domain.effect.ShieldEffect;
import it.unicam.cs.mpgc.rpg123024.domain.factory.StandardItemCatalog;
import it.unicam.cs.mpgc.rpg123024.domain.factory.StandardMonsterFactory;
import it.unicam.cs.mpgc.rpg123024.domain.map.DungeonGenerator;
import it.unicam.cs.mpgc.rpg123024.domain.map.RandomDungeonGenerator;
import it.unicam.cs.mpgc.rpg123024.domain.model.Stats;
import it.unicam.cs.mpgc.rpg123024.domain.random.SeededRandomSource;
import it.unicam.cs.mpgc.rpg123024.gui.event.GameEventBus;
import it.unicam.cs.mpgc.rpg123024.gui.javafx.FxCombatView;
import it.unicam.cs.mpgc.rpg123024.gui.javafx.FxConfirmDialog;
import it.unicam.cs.mpgc.rpg123024.gui.javafx.FxExplorationView;
import it.unicam.cs.mpgc.rpg123024.gui.javafx.FxInventoryView;
import it.unicam.cs.mpgc.rpg123024.gui.javafx.FxMenuView;
import it.unicam.cs.mpgc.rpg123024.gui.javafx.FxNavigator;
import it.unicam.cs.mpgc.rpg123024.gui.javafx.FxNavigator.Screen;
import it.unicam.cs.mpgc.rpg123024.gui.presenter.CombatPresenter;
import it.unicam.cs.mpgc.rpg123024.gui.presenter.ExitPresenter;
import it.unicam.cs.mpgc.rpg123024.gui.presenter.ExplorationPresenter;
import it.unicam.cs.mpgc.rpg123024.gui.presenter.GameLog;
import it.unicam.cs.mpgc.rpg123024.gui.presenter.GameSessionHolder;
import it.unicam.cs.mpgc.rpg123024.gui.presenter.InventoryPresenter;
import it.unicam.cs.mpgc.rpg123024.gui.presenter.MenuPresenter;
import it.unicam.cs.mpgc.rpg123024.gui.presenter.NamedAction;
import it.unicam.cs.mpgc.rpg123024.persistence.dto.SnapshotMapper;
import it.unicam.cs.mpgc.rpg123024.persistence.json.JsonGameRepository;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.List;

/**
 * Punto di avvio dell'applicazione e composition root.
 * <p>
 * <b>Responsabilità:</b> unico punto in cui si scelgono e istanziano le implementazioni concrete
 * (generatore, cataloghi, AI, repository JSON, View JavaFX) e si collegano tra loro tramite
 * iniezione delle dipendenze manuale; definisce la configurazione di gioco (dimensione della mappa,
 * probabilità, statistiche iniziali, azioni dell'eroe). Il seed della partita si può fissare con la
 * proprietà di sistema {@code rpg.seed}.
 * <p>
 * <b>Pattern:</b> Composition Root / Dependency Injection manuale. <b>SOLID:</b> Dependency
 * Inversion (tutti gli altri componenti ricevono astrazioni dall'esterno); Open/Closed (cambiare
 * supporto di salvataggio, interfaccia grafica o contenuti significa cambiare solo questa classe).
 */
public class Main extends Application {

    private static final int DUNGEON_ROWS = 5;
    private static final int DUNGEON_COLS = 5;
    private static final double MONSTER_PROBABILITY = 0.4;
    private static final double LOOT_PROBABILITY = 0.3;
    private static final Stats HERO_STATS = new Stats(100, 10, 3, 5);
    private static final int HEAL_COOLDOWN_TURNS = 2;
    private static final Path SAVE_FILE = Path.of(System.getProperty("user.home"), ".rpg123024", "save.json");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        long seed = Long.getLong("rpg.seed", System.nanoTime());
        StandardItemCatalog items = new StandardItemCatalog();
        MonsterAI monsterAI = new AggressiveMonsterAI();
        DungeonGenerator generator = new RandomDungeonGenerator(DUNGEON_ROWS, DUNGEON_COLS,
                MONSTER_PROBABILITY, LOOT_PROBABILITY, new SeededRandomSource(seed),
                new StandardMonsterFactory(), items);
        GameRepository repository = new JsonGameRepository(SAVE_FILE, new SnapshotMapper(items, monsterAI));

        GameEventBus events = new GameEventBus();
        GameLog log = new GameLog();
        events.subscribe(log);
        GameSessionHolder sessions = new GameSessionHolder();

        Scene scene = new Scene(new Pane(), 1040, 700);
        FxNavigator navigator = new FxNavigator(scene);

        FxMenuView menuView = new FxMenuView();
        MenuPresenter menu = new MenuPresenter(menuView, new StartNewGame(generator, monsterAI, HERO_STATS),
                new LoadGame(repository), sessions, log, navigator);
        menuView.setPresenter(menu);

        FxExplorationView explorationView = new FxExplorationView();
        ExplorationPresenter exploration = new ExplorationPresenter(explorationView, new MoveToRoom(events),
                new SaveGame(repository), sessions, log, navigator);
        explorationView.setPresenter(exploration);

        FxCombatView combatView = new FxCombatView();
        CombatPresenter combat = new CombatPresenter(combatView, new ExecuteTurn(events), sessions, log,
                navigator, heroActions());
        combatView.setPresenter(combat);

        FxInventoryView inventoryView = new FxInventoryView();
        InventoryPresenter inventory = new InventoryPresenter(inventoryView, new UseItem(events), sessions, navigator);
        inventoryView.setPresenter(inventory);

        ExitPresenter exit = new ExitPresenter(new FxConfirmDialog(), sessions);
        stage.setOnCloseRequest(event -> {
            if (!exit.onExitRequested()) {
                event.consume();
            }
        });

        navigator.register(Screen.MENU, menuView.root(), () -> {
            menuView.clearStatus();
            menu.refresh();
        });
        navigator.register(Screen.EXPLORATION, explorationView.root(), exploration::refresh);
        navigator.register(Screen.COMBAT, combatView.root(), combat::refresh);
        navigator.register(Screen.INVENTORY, inventoryView.root(), inventory::refresh);
        navigator.setGameEndHandler(menu::showOutcome);

        stage.setTitle("Dungeon Crawler");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(620);
        navigator.showMenu();
        stage.show();
    }

    private static List<NamedAction> heroActions() {
        return List.of(
                new NamedAction("Attack", new AbilityAction(new PhysicalAttack(), Targeting.OPPONENT)),
                new NamedAction("Heal", new CooldownAction("Heal",
                        new AbilityAction(new HealAbility("Heal", 20), Targeting.SELF), HEAL_COOLDOWN_TURNS)),
                new NamedAction("Shield", new ApplyEffectAction("Shield",
                        target -> new ShieldEffect(target, 4, 3), Targeting.SELF)),
                new NamedAction("Poison", new ApplyEffectAction("Poison",
                        target -> new PoisonEffect(target, 5, 3), Targeting.OPPONENT)));
    }
}

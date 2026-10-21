package it.unicam.cs.mpgc.rpg123024.gui.presenter;

import it.unicam.cs.mpgc.rpg123024.domain.combat.ActionResult;
import it.unicam.cs.mpgc.rpg123024.domain.event.GameEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Registro testuale degli eventi di gioco, osservatore del bus degli eventi.
 * <p>
 * <b>Responsabilità:</b> tradurre ogni {@link GameEvent} in un messaggio leggibile e conservare gli
 * ultimi messaggi, che i Presenter passano alle View. È l'unico punto in cui gli eventi diventano
 * testo.
 * <p>
 * <b>Pattern:</b> Observer (osservatore concreto). <b>SOLID:</b> Single Responsibility (solo
 * formattazione e memoria dei messaggi); lo {@code switch} esaustivo sull'interfaccia sealed fa sì
 * che un nuovo tipo di evento non gestito sia un errore di compilazione.
 */
public class GameLog implements Consumer<GameEvent> {

    static final int MAX_MESSAGES = 50;

    private final List<String> messages = new ArrayList<>();

    @Override
    public void accept(GameEvent event) {
        messages.add(format(event));
        if (messages.size() > MAX_MESSAGES) {
            messages.remove(0);
        }
    }

    public List<String> messages() {
        return Collections.unmodifiableList(messages);
    }

    public void clear() {
        messages.clear();
    }

    static String format(GameEvent event) {
        return switch (event) {
            case GameEvent.RoomEntered e -> "Entered room (" + e.position().row() + ", " + e.position().col() + ")";
            case GameEvent.ItemCollected e -> "Found " + e.item().name();
            case GameEvent.CombatStarted e -> "A " + e.monsterName() + " attacks!";
            case GameEvent.ActionPerformed e -> format(e.result());
            case GameEvent.CombatEnded e -> switch (e.outcome()) {
                case HERO_WON -> "The enemy is defeated";
                case HERO_DEFEATED -> "You have been defeated";
                case ONGOING -> "The combat goes on";
            };
            case GameEvent.LevelUp e -> "Level up! You are now level " + e.newLevel();
            case GameEvent.GameWon e -> "You found the exit!";
            case GameEvent.GameOver e -> "Game over";
        };
    }

    private static String format(ActionResult result) {
        String text = result.actorName() + " uses " + result.actionName();
        if (!result.targetName().equals(result.actorName())) {
            text += " on " + result.targetName();
        }
        return result.amount() > 0 ? text + " (" + result.amount() + ")" : text;
    }
}

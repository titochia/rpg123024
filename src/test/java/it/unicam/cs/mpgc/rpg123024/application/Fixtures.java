package it.unicam.cs.mpgc.rpg123024.application;

import it.unicam.cs.mpgc.rpg123024.application.state.GameSession;
import it.unicam.cs.mpgc.rpg123024.domain.ability.PhysicalAttack;
import it.unicam.cs.mpgc.rpg123024.domain.combat.AbilityAction;
import it.unicam.cs.mpgc.rpg123024.domain.combat.AggressiveMonsterAI;
import it.unicam.cs.mpgc.rpg123024.domain.combat.Targeting;
import it.unicam.cs.mpgc.rpg123024.domain.combat.TurnAction;
import it.unicam.cs.mpgc.rpg123024.domain.map.Dungeon;
import it.unicam.cs.mpgc.rpg123024.domain.map.Position;
import it.unicam.cs.mpgc.rpg123024.domain.model.Hero;
import it.unicam.cs.mpgc.rpg123024.domain.model.Item;
import it.unicam.cs.mpgc.rpg123024.domain.model.Monster;
import it.unicam.cs.mpgc.rpg123024.domain.model.Stats;

public final class Fixtures {

    public static final TurnAction ATTACK = new AbilityAction(new PhysicalAttack(), Targeting.OPPONENT);

    private Fixtures() {
    }

    public static Hero hero() {
        return new Hero("Hero", new Stats(100, 10, 2, 5));
    }

    public static Monster monster(int health, int attack, int speed, int experienceReward) {
        return new Monster("Orc", new Stats(health, attack, 1, speed), experienceReward);
    }

    public static Dungeon dungeon() {
        return new Dungeon(2, 2, new Position(0, 0), new Position(1, 1));
    }

    public static Dungeon dungeonWithExitAt(Position exit) {
        return new Dungeon(2, 2, new Position(0, 0), exit);
    }

    public static GameSession session(Hero hero, Dungeon dungeon) {
        return new GameSession(hero, dungeon, new AggressiveMonsterAI());
    }

    public static Item potion() {
        return new Item("potion", "Potion", "Heals 30 HP", target -> target.heal(30));
    }
}
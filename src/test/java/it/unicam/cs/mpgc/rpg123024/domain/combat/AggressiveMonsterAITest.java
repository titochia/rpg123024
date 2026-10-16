package it.unicam.cs.mpgc.rpg123024.domain.combat;

import it.unicam.cs.mpgc.rpg123024.domain.model.Hero;
import it.unicam.cs.mpgc.rpg123024.domain.model.Monster;
import it.unicam.cs.mpgc.rpg123024.domain.model.Stats;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AggressiveMonsterAITest {

    @Test
    void alwaysAttacksTheOpponent() {
        Monster monster = new Monster("Orc", new Stats(30, 6, 1, 3), 50);
        Hero hero = new Hero("Hero", new Stats(100, 10, 2, 5));
        Fighter self = new Fighter(monster);
        Fighter opponent = new Fighter(hero);

        ActionResult result = new AggressiveMonsterAI().chooseAction(self, opponent).execute(self, opponent);

        assertEquals(4, result.amount());
        assertEquals("Hero", result.targetName());
        assertEquals(96, hero.currentHealth());
    }
}
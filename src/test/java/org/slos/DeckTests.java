package org.slos;

import org.slos.battle.board.Deck;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.domain.monster.DamageType;
import org.slos.splinterlands.domain.monster.MonsterType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class DeckTests {
    private final Integer DECK_SIZE = 7;

    @Test
    public void itShouldSetADeck() {
        MonsterBattleStats summoner = new MonsterBattleStats(1, 1, MonsterType.SUMMONER, DamageType.ATTACK, 2, 3, 4, 5, null);
        List<MonsterBattleStats> monsters = new ArrayList<>();
        for (int x = 0; x < DECK_SIZE; x++) {
            monsters.add(new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 2, 3, 4, 5, null));
        }

        Deck deck = new Deck(summoner, monsters);
    }
}

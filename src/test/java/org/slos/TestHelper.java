package org.slos;

import org.slos.battle.abilities.AbilityFactory;
import org.slos.battle.board.Board;
import org.slos.battle.board.Deck;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.domain.monster.DamageType;
import org.slos.splinterlands.domain.monster.MonsterType;

import java.util.ArrayList;
import java.util.List;

public interface TestHelper {

    AbilityFactory DEFAULT_ABILITY_FACTORY = new AbilityFactory();

    default Board getEmptyBoard() {
        int boardSize = 7;
        Board board = new Board(boardSize);

        return board;
    }

    default MonsterBattleStats getDefaultMonsterBattleStats() {
        return new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 1, 1, 1, null);
    }

    default Board getDefaultBoard() {
        AbilityFactory abilityFactory = new AbilityFactory();

        int boardSize = 7;
        Board board = new Board(boardSize);

        MonsterBattleStats summoner1 = new MonsterBattleStats(1, 3, MonsterType.SUMMONER, DamageType.NONE, 0, 0, 1, 0, null);
        MonsterBattleStats summoner2 = new MonsterBattleStats(1, 3, MonsterType.SUMMONER, DamageType.NONE, 0, 0, 1, 0, null);

        List<MonsterBattleStats> topMonsters = new ArrayList<>();
        List<MonsterBattleStats> bottomMonsters = new ArrayList<>();

        int x = 3;

        topMonsters.add(new MonsterBattleStats(1, x, MonsterType.MONSTER, DamageType.ATTACK, x+1, x+1, x+1, x, null));
        topMonsters.add(new MonsterBattleStats(1, x, MonsterType.MONSTER, DamageType.ATTACK, x+1, x+1, x+1, x, null));
        topMonsters.add(new MonsterBattleStats(1, x, MonsterType.MONSTER, DamageType.ATTACK, x+1, x+1, x+1, x, null));
        topMonsters.add(new MonsterBattleStats(1, x, MonsterType.MONSTER, DamageType.ATTACK, x+1, x+1, x+1, x, null));
        topMonsters.add(new MonsterBattleStats(1, x, MonsterType.MONSTER, DamageType.ATTACK, x+1, x+1, x+1, x, null));
        topMonsters.add(new MonsterBattleStats(1, x, MonsterType.MONSTER, DamageType.ATTACK, x+1, x+1, x+1, x, null));

        bottomMonsters.add(new MonsterBattleStats(1, x, MonsterType.MONSTER, DamageType.ATTACK, x+1, x+1, x+1, x, null));
        bottomMonsters.add(new MonsterBattleStats(1, x, MonsterType.MONSTER, DamageType.ATTACK, x+1, x+1, x+1, x, null));
        bottomMonsters.add(new MonsterBattleStats(1, x, MonsterType.MONSTER, DamageType.ATTACK, x+1, x+1, x+1, x, null));
        bottomMonsters.add(new MonsterBattleStats(1, x, MonsterType.MONSTER, DamageType.ATTACK, x+1, x+1, x+1, x, null));
        bottomMonsters.add(new MonsterBattleStats(1, x, MonsterType.MONSTER, DamageType.ATTACK, x+1, x+1, x+1, x, null));
        bottomMonsters.add(new MonsterBattleStats(1, x, MonsterType.MONSTER, DamageType.ATTACK, x+1, x+1, x+1, x, null));

        Deck topDeck = new Deck(summoner1, topMonsters);
        Deck bottomDeck = new Deck(summoner2, bottomMonsters);

        board.populateBoard(topDeck, bottomDeck);

        return board;
    }
}

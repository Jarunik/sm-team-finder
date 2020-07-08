package org.slos;

import org.slos.battle.BattleMaster;
import org.slos.battle.GameContext;
import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityFactory;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.buff.BuffService;
import org.slos.battle.board.Board;
import org.slos.battle.board.Deck;
import org.slos.battle.decision.MasterChoiceContext;
import org.slos.battle.decision.strategy.ChoiceStrategyMode;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.battle.phase.GameInitializationPhase;
import org.slos.battle.phase.RoundPhase;
import org.slos.splinterlands.domain.GameRuleType;
import org.slos.splinterlands.domain.monster.DamageType;
import org.slos.splinterlands.domain.monster.MonsterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameInitializationPhaseTests implements TestHelper {
    private GameContext DEFAULT_GAME_CONTEXT;
    private GameInitializationPhase DEFAULT_GAME_INITIALIZATION_PHASE;
    private final AbilityFactory DEFAULT_ABILITY_FACTORY = new AbilityFactory();
    private Board DEFAULT_BOARD;
    private BuffService DEFAULT_BUFF_SERVICE;

    @BeforeEach
    public void setup() {

    }

    @Test
    public void itShouldSetInitialAttackOrder() {
        MonsterBattleStats summoner1;
        MonsterBattleStats summoner2;
        summoner1 = new MonsterBattleStats(1, 1, MonsterType.SUMMONER, DamageType.MAGIC, 1, 0, 1, 1, null);
        summoner2 = new MonsterBattleStats(1, 1, MonsterType.SUMMONER, DamageType.MAGIC, 1, 0, 1, 1, null);
        List<MonsterBattleStats> topMonsters = new ArrayList<>();
        List<MonsterBattleStats> bottomMonsters = new ArrayList<>();
        topMonsters.add(new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 1, 1, null));
        topMonsters.add(new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null));

        bottomMonsters.add(new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 1, 1, null));
        bottomMonsters.add(new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 1, 1, null));

        Deck topDeck = null;
        Deck bottomDeck = null;
        topDeck = new Deck(summoner1, topMonsters);
        bottomDeck = new Deck(summoner2, bottomMonsters);
        MasterChoiceContext masterChoiceContext = new MasterChoiceContext();
        GameContext gameContext;
        Board board = null;
        Set<GameRuleType> gameRules = new HashSet<>();
        board = new Board(7);
        board.populateBoard(topDeck, bottomDeck);

        gameContext = new GameContext(board, gameRules);
        gameContext.setMasterChoiceContext(masterChoiceContext);
        gameContext.setChoiceStrategyMode(ChoiceStrategyMode.RANDOM_WEIGHTED);
        BattleMaster battleMaster = new BattleMaster();

        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        gameInitializationPhase.execute(gameContext);
    }

    @Test
    public void itShouldApplyTheSummonerBuffs() {
        DEFAULT_BOARD = getDefaultBoard();
        DEFAULT_GAME_CONTEXT =  new GameContext(DEFAULT_BOARD, new HashSet<>());
        DEFAULT_GAME_INITIALIZATION_PHASE = new GameInitializationPhase();
        DEFAULT_BUFF_SERVICE = DEFAULT_GAME_CONTEXT.getBuffService();

        List<Ability> summonerAbilities = new ArrayList<>();
        summonerAbilities.add(DEFAULT_ABILITY_FACTORY.getSummonerAbility(AbilityType.SUMMONER_HEALTH, 1));
        summonerAbilities.add(DEFAULT_ABILITY_FACTORY.getSummonerAbility(AbilityType.SUMMONER_ARMOR, 3));
        summonerAbilities.add(DEFAULT_ABILITY_FACTORY.getSummonerAbility(AbilityType.SUMMONER_RANGED, 1));
        summonerAbilities.add(DEFAULT_ABILITY_FACTORY.getSummonerAbility(AbilityType.SUMMONER_SPEED, 1));
        summonerAbilities.add(DEFAULT_ABILITY_FACTORY.getSummonerAbility(AbilityType.SUMMONER_ATTACK, 1));
        summonerAbilities.add(DEFAULT_ABILITY_FACTORY.getSummonerAbility(AbilityType.SUMMONER_MAGIC, 1));
        MonsterBattleStats summonerWithBuffAbilities = new MonsterBattleStats(1, 1, MonsterType.SUMMONER, DamageType.NONE, 0, 0, 0, 0, summonerAbilities);
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.NONE, 0, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(0, summonerWithBuffAbilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop1);
        DEFAULT_BOARD.getBoardTop().placeInSection(3, monsterBattleStatsTop2);
        DEFAULT_BOARD.getBoardTop().placeInSection(4, monsterBattleStatsTop3);
        DEFAULT_BOARD.getBoardTop().placeInSection(5, monsterBattleStatsTop4);
        DEFAULT_BOARD.getBoardTop().placeInSection(6, null);

        DEFAULT_GAME_INITIALIZATION_PHASE.execute(DEFAULT_GAME_CONTEXT);

        assertEquals(4, DEFAULT_BUFF_SERVICE.getBuffsAppliedTo(monsterBattleStatsTop).size());
        assertEquals(Integer.valueOf(2), monsterBattleStatsTop.getHealth().getValue());
        assertEquals(Integer.valueOf(3), monsterBattleStatsTop.getArmor().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsTop.getSpeed().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsTop.getDamageValue(DamageType.ATTACK).getValue());
        assertEquals(4, DEFAULT_BUFF_SERVICE.getBuffsAppliedTo(monsterBattleStatsTop1).size());
        assertEquals(Integer.valueOf(2), monsterBattleStatsTop1.getHealth().getValue());
        assertEquals(Integer.valueOf(3), monsterBattleStatsTop1.getArmor().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsTop1.getSpeed().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsTop1.getDamageValue(DamageType.MAGIC).getValue());
        assertEquals(4, DEFAULT_BUFF_SERVICE.getBuffsAppliedTo(monsterBattleStatsTop2).size());
        assertEquals(Integer.valueOf(2), monsterBattleStatsTop2.getHealth().getValue());
        assertEquals(Integer.valueOf(3), monsterBattleStatsTop2.getArmor().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsTop2.getSpeed().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsTop2.getDamageValue(DamageType.RANGED).getValue());
        assertEquals(3, DEFAULT_BUFF_SERVICE.getBuffsAppliedTo(monsterBattleStatsTop3).size());
        assertEquals(Integer.valueOf(2), monsterBattleStatsTop3.getHealth().getValue());
        assertEquals(Integer.valueOf(3), monsterBattleStatsTop3.getArmor().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsTop3.getSpeed().getValue());
        assertEquals(Integer.valueOf(0), monsterBattleStatsTop3.getDamageValue(DamageType.NONE).getValue());
        assertEquals(4, DEFAULT_BUFF_SERVICE.getBuffsAppliedTo(monsterBattleStatsTop4).size());
        assertEquals(Integer.valueOf(2), monsterBattleStatsTop4.getHealth().getValue());
        assertEquals(Integer.valueOf(3), monsterBattleStatsTop4.getArmor().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsTop4.getSpeed().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsTop4.getDamageValue(DamageType.ATTACK).getValue());
    }

    @Test
    public void itShouldApplyTheSummonerAbilityBuffs() {
        DEFAULT_BOARD = getDefaultBoard();
        DEFAULT_GAME_CONTEXT =  new GameContext(DEFAULT_BOARD, new HashSet<>());
        DEFAULT_GAME_INITIALIZATION_PHASE = new GameInitializationPhase();
        DEFAULT_BUFF_SERVICE = DEFAULT_GAME_CONTEXT.getBuffService();

        List<Ability> summonerAbilities = new ArrayList<>();
        summonerAbilities.add(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.VOID));
        summonerAbilities.add(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.AFFLICTION));
        summonerAbilities.add(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.BLAST));

        MonsterBattleStats summonerWithBuffAbilities = new MonsterBattleStats(1, 1, MonsterType.SUMMONER, DamageType.NONE, 0, 0, 0, 0, summonerAbilities);
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.NONE, 0, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(0, summonerWithBuffAbilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop1);
        DEFAULT_BOARD.getBoardTop().placeInSection(3, monsterBattleStatsTop2);
        DEFAULT_BOARD.getBoardTop().placeInSection(4, monsterBattleStatsTop3);
        DEFAULT_BOARD.getBoardTop().placeInSection(5, monsterBattleStatsTop4);
        DEFAULT_BOARD.getBoardTop().placeInSection(6, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);

        DEFAULT_GAME_INITIALIZATION_PHASE.execute(DEFAULT_GAME_CONTEXT);

        System.out.println("Top Buffs: " + monsterBattleStatsTop.getAbilities());
        System.out.println("Bottom Buffs: " + monsterBattleStatsBottom.getAbilities());
    }

    @Test
    public void itShouldApplyTheSummonerAbilityBuffsToDualDamageMonsters() {
        DEFAULT_BOARD = getDefaultBoard();
        DEFAULT_GAME_CONTEXT =  new GameContext(DEFAULT_BOARD, new HashSet<>());
        DEFAULT_GAME_INITIALIZATION_PHASE = new GameInitializationPhase();
        DEFAULT_BUFF_SERVICE = DEFAULT_GAME_CONTEXT.getBuffService();

        List<Ability> summonerAbilities = new ArrayList<>();
        summonerAbilities.add(DEFAULT_ABILITY_FACTORY.getSummonerAbility(AbilityType.SUMMONER_RANGED, 1));

        MonsterBattleStats summonerWithBuffAbilities = new MonsterBattleStats(1, 1, MonsterType.SUMMONER, DamageType.NONE, 0, 0, 0, 0, summonerAbilities);
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        monsterBattleStatsTop.addDamage(DamageType.RANGED, 1);
        MonsterBattleStats monsterBattleStatsTop1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.NONE, 0, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsTop4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(0, summonerWithBuffAbilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop1);
        DEFAULT_BOARD.getBoardTop().placeInSection(3, monsterBattleStatsTop2);
        DEFAULT_BOARD.getBoardTop().placeInSection(4, monsterBattleStatsTop3);
        DEFAULT_BOARD.getBoardTop().placeInSection(5, monsterBattleStatsTop4);
        DEFAULT_BOARD.getBoardTop().placeInSection(6, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);

        DEFAULT_GAME_INITIALIZATION_PHASE.execute(DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(2), monsterBattleStatsTop.getDamageValue(DamageType.RANGED).getValue());
    }

    @Test
    public void summonerAfflictionShouldPreventHealing() {
        DEFAULT_BOARD = getDefaultBoard();
        DEFAULT_GAME_CONTEXT =  new GameContext(DEFAULT_BOARD, new HashSet<>());
        DEFAULT_GAME_INITIALIZATION_PHASE = new GameInitializationPhase();
        DEFAULT_BUFF_SERVICE = DEFAULT_GAME_CONTEXT.getBuffService();

        List<Ability> summonerAbilities = new ArrayList<>();
        summonerAbilities.add(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.AFFLICTION));
        List<Ability> tankHeal = new ArrayList<>();
        tankHeal.add(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.TANK_HEAL));

        MonsterBattleStats summonerWithBuffAbilities = new MonsterBattleStats(1, 1, MonsterType.SUMMONER, DamageType.NONE, 0, 0, 0, 0, summonerAbilities);
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 3, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 3, null);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, tankHeal);
        DEFAULT_BOARD.getBoardTop().placeInSection(0, summonerWithBuffAbilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(3, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(4, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(5, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(6, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(6, null);

        DEFAULT_GAME_INITIALIZATION_PHASE.execute(DEFAULT_GAME_CONTEXT);

        RoundPhase roundPhase = new RoundPhase();
        roundPhase.executeTurn(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(9), monsterBattleStatsBottom.getHealth().getValue());
    }
}

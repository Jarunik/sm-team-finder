package org.slos;

import org.slos.battle.BattleMaster;
import org.slos.battle.GameContext;
import org.slos.battle.GameWinner;
import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.abilities.attack.AfflictedAbility;
import org.slos.battle.abilities.attack.IsPoisonedAbility;
import org.slos.battle.abilities.attack.IsStunnedAbility;
import org.slos.battle.abilities.rule.turn.OnTurnStartRule;
import org.slos.battle.board.Board;
import org.slos.battle.board.Deck;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SituationalTests implements TestHelper {
    private Board DEFAULT_BOARD = null;
    private GameContext DEFAULT_GAME_CONTEXT = null;

    @BeforeEach
    public void setup() {
        DEFAULT_BOARD = getDefaultBoard();
        Set<GameRuleType> gameRules = new HashSet<>();
        DEFAULT_GAME_CONTEXT = new GameContext(DEFAULT_BOARD, gameRules);
        DEFAULT_GAME_CONTEXT.setChoiceStrategyMode(ChoiceStrategyMode.FIRST_CONFIGURED);
        DEFAULT_GAME_CONTEXT.resetAttackQueue();
    }

    @Test
    public void itShouldClearMonsterWhenDiesFromReflect() {
        List<Ability> abilities = Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.MAGIC_REFLECT));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 100, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(2, 1, MonsterType.MONSTER, DamageType.MAGIC, 5, 0, 2, 1, null);

        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom1);

        DEFAULT_GAME_CONTEXT.getAttackService().attack(monsterBattleStatsBottom1, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);

        System.out.println(DEFAULT_BOARD.toSimpleString());
        assertTrue(monsterBattleStatsBottom1.isDead());
        long deadFromReflect = DEFAULT_GAME_CONTEXT.getAllMonsters().stream().filter(monsterBattleStats -> monsterBattleStats == monsterBattleStatsBottom1).count();
        assertEquals(0, deadFromReflect);
    }

    @Test
    public void itShouldAttackTwiceWhenKillingWithDoubleStrike() {
        List<Ability> abilities = Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.DOUBLE_STRIKE));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 100, 0, 100, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(2, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(3, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 101, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        System.out.println(DEFAULT_GAME_CONTEXT);

        RoundPhase roundPhase = new RoundPhase();
        roundPhase.executeTurn(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertTrue(monsterBattleStatsBottom.isDead());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom2.getHealth().getValue());
        assertSame(monsterBattleStatsBottom2, DEFAULT_BOARD.getBoardBottom().peekFromLocation(1));
    }

    @Test
    public void voidShouldWorkAgainstReflect() {
        List<Ability> magicReflect =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.MAGIC_REFLECT));
        List<Ability> voidAbility =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.VOID));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(2, 1, MonsterType.MONSTER, DamageType.MAGIC, 6, 0, 100, 1, voidAbility);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(3, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 100, 1, magicReflect);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(100), monsterBattleStatsTop.getHealth().getValue());
        assertEquals(Integer.valueOf(100), monsterBattleStatsBottom.getHealth().getValue());

        DEFAULT_GAME_CONTEXT.getAttackService().attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(98), monsterBattleStatsTop.getHealth().getValue());
        assertEquals(Integer.valueOf(94), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void redemptionShouldRegisterDeathIfKillsEnemy() {
        List<Ability> redemption =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.REDEMPTION));
        List<Ability> shield =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SHIELD));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 0, 0, 1, 1, redemption);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(2, 1, MonsterType.MONSTER, DamageType.ATTACK, 100, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(3, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(4, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(5, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 3, 4, 1, null);
        MonsterBattleStats monsterBattleStatsBottom5 = new MonsterBattleStats(6, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 4, 1, shield);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, monsterBattleStatsBottom5);
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);
        System.out.println(DEFAULT_GAME_CONTEXT);
        RoundPhase roundPhase = new RoundPhase();

        roundPhase.executeTurn(monsterBattleStatsBottom1, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);
//        DEFAULT_GAME_CONTEXT.getAttackService().attack(monsterBattleStatsBottom1, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        DEFAULT_GAME_CONTEXT.printHistory();
        assertEquals(Integer.valueOf(4), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getId());
    }

    @Test
    public void voidShouldWorkAgainstReflect2() {
        List<Ability> magicReflect =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.MAGIC_REFLECT));
        List<Ability> voidAbility =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.VOID));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(2, 1, MonsterType.MONSTER, DamageType.MAGIC, 8, 0, 100, 1, voidAbility);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(3, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 100, 1, magicReflect);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(100), monsterBattleStatsTop.getHealth().getValue());
        assertEquals(Integer.valueOf(100), monsterBattleStatsBottom.getHealth().getValue());

        DEFAULT_GAME_CONTEXT.getAttackService().attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);

        DEFAULT_GAME_CONTEXT.printHistory();
        assertEquals(Integer.valueOf(92), monsterBattleStatsBottom.getHealth().getValue());
        assertEquals(Integer.valueOf(98), monsterBattleStatsTop.getHealth().getValue());
    }

    @Test
    public void itShouldApplyReflectToBlastFromAdjacentTargets() {
        List<Ability> blast = Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.BLAST));
        List<Ability> magicReflect = Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.MAGIC_REFLECT));

        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 100, 0, 1000, 1, blast);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 101, 1, magicReflect);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 101, 1, magicReflect);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 101, 1, magicReflect);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, magicReflect);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);

        DEFAULT_GAME_CONTEXT.getAttackService().attackSpecific(monsterBattleStatsTop, DamageType.MAGIC, monsterBattleStatsBottom2, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(51), monsterBattleStatsBottom3.getHealth().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom4.getHealth().getValue());

        assertEquals(Integer.valueOf(900), monsterBattleStatsTop.getHealth().getValue());
    }

    @Test
    public void itShouldNotReflectDamageWhenHealed() {
        List<Ability> abilities =  new ArrayList<>();
        List<Ability> tankHeal = Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.TANK_HEAL));
        abilities.add(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.HEAL));
        abilities.add(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.MAGIC_REFLECT));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 10, 0, 10, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 10, 0, 10, 1, tankHeal);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getHealth().getValue());
        DEFAULT_GAME_CONTEXT.getAttackService().attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(6), monsterBattleStatsBottom.getHealth().getValue());

        ((OnTurnStartRule)monsterBattleStatsBottom.getAbilities().get(0).getEffect()).executeOnTurnStart(monsterBattleStatsBottom, DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(9), monsterBattleStatsBottom.getHealth().getValue());

        ((OnTurnStartRule)monsterBattleStatsBottom2.getAbilities().get(0).getEffect()).executeOnTurnStart(monsterBattleStatsBottom, DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom.getHealth().getValue());
    }

    @Test
    public void itShouldNotRetaliateWhenAttackerIsDead() {
        List<Ability> retaliate =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.RETALIATE));
        List<Ability> thorns =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.THORNS));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 0, 1, thorns);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(1, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 100, 1, retaliate);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(100), monsterBattleStatsBottom.getHealth().getValue());
        assertEquals(Integer.valueOf(100), monsterBattleStatsBottom.getHealth().getValue());

        System.out.println(DEFAULT_BOARD.getBoardBottom());
        DEFAULT_GAME_CONTEXT.getAttackService().attack(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);
        System.out.println(DEFAULT_BOARD.getBoardBottom());

        assertEquals(Integer.valueOf(90), monsterBattleStatsBottom.getHealth().getValue());
        assertEquals(Integer.valueOf(0), monsterBattleStatsTop.getHealth().getValue());
    }

    @Test
    public void blastDamageShouldApplyEnrage() {
        List<Ability> blast = Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.BLAST));
        List<Ability> enrage1 =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.ENRAGE));
        List<Ability> enrage2 =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.ENRAGE));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 1, 1, blast);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(2, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 10, 1, enrage1);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(3, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(4, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 10, 1, enrage2);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();

        DEFAULT_GAME_CONTEXT.getAttackService().attackSpecific(monsterBattleStatsTop, DamageType.MAGIC, monsterBattleStatsBottom2, DEFAULT_GAME_CONTEXT);


        assertEquals(Integer.valueOf(8), monsterBattleStatsBottom3.getHealth().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom3.getSpeed().getValue());
        assertEquals(Integer.valueOf(15), monsterBattleStatsBottom3.getDamageValue(DamageType.ATTACK).getValue());

        assertEquals(Integer.valueOf(8), monsterBattleStatsBottom1.getHealth().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom1.getSpeed().getValue());
        assertEquals(Integer.valueOf(15), monsterBattleStatsBottom1.getDamageValue(DamageType.ATTACK).getValue());
    }

    @Test
    public void itShouldDrawIfFatigureKillsBothTeams() {
        Board board = new Board(7);
        MonsterBattleStats summonerTop = new MonsterBattleStats(1, 1, MonsterType.SUMMONER, DamageType.NONE, 0, 0, 1, 1, null);
        MonsterBattleStats summonerBottom = new MonsterBattleStats(1, 1, MonsterType.SUMMONER, DamageType.NONE, 0, 0, 1, 1, null);
        MonsterBattleStats topMonster = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.NONE, 0, 0, 1, 10, null);
        MonsterBattleStats bottomMonster = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.NONE, 0, 0, 1, 10, null);
        Deck top = new Deck(summonerTop, topMonster);
        Deck bottom = new Deck(summonerBottom, bottomMonster);
        board.populateBoard(top, bottom);
        GameContext gameContext = new GameContext(board, null);
        BattleMaster battleMaster = new BattleMaster();

        battleMaster.runGame(gameContext);

        assertTrue(gameContext.gameHasWinner());
        assertTrue(gameContext.getAllMonsters().size() == 0);
        assertEquals(GameWinner.DRAW, gameContext.getGameWinner());
    }

    @Test
    public void itShouldRemoveMonsterWhenHealthAfterBuffEqualsZero() {
        List<Ability> strengthen = Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.STRENGTHEN));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 100, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(2, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 2, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(3, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 101, 1, strengthen);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        System.out.println(DEFAULT_GAME_CONTEXT);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(3), monsterBattleStatsBottom.getHealth().getValue());
        DEFAULT_GAME_CONTEXT.getAttackService().attackSpecific(monsterBattleStatsTop, DamageType.MAGIC, monsterBattleStatsBottom, DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom.getHealth().getValue());
        DEFAULT_GAME_CONTEXT.getAttackService().attackSpecific(monsterBattleStatsTop, DamageType.MAGIC, monsterBattleStatsBottom, DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom.getHealth().getValue());

        RoundPhase roundPhase = new RoundPhase();
        roundPhase.executeTurn(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(0), monsterBattleStatsBottom.getHealth().getValue());
        assertTrue(monsterBattleStatsBottom.isDead());
    }

    @Test
    public void itShouldHaveWinnerIfFatigueKillsOffOneTeam() {
        Board board = new Board(7);
        MonsterBattleStats summonerTop = new MonsterBattleStats(1, 1, MonsterType.SUMMONER, DamageType.NONE, 0, 0, 1, 1, null);
        MonsterBattleStats summonerBottom = new MonsterBattleStats(1, 1, MonsterType.SUMMONER, DamageType.NONE, 0, 0, 1, 1, null);
        MonsterBattleStats topMonster = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.NONE, 0, 0, 1000, 1, null);
        MonsterBattleStats bottomMonster = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.NONE, 0, 0, 10, 1, null);
        Deck top = new Deck(summonerTop, topMonster);
        Deck bottom = new Deck(summonerBottom, bottomMonster);
        board.populateBoard(top, bottom);
        GameContext gameContext = new GameContext(board, null);
        BattleMaster battleMaster = new BattleMaster();
        gameContext.setTrackHistory(true);

        battleMaster.runGame(gameContext);
        gameContext.printHistory();

        assertTrue(gameContext.gameHasWinner());
        assertTrue(gameContext.getAllMonsters().size() == 1);
        assertEquals(GameWinner.TOP, gameContext.getGameWinner());
    }

    @Test
    public void reflectShouldNotBeReflected() {
        List<Ability> abilities = Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.THORNS));
        Board board = new Board(7);
        MonsterBattleStats summonerTop = new MonsterBattleStats(1, 1, MonsterType.SUMMONER, DamageType.NONE, 0, 0, 1, 1, null);
        MonsterBattleStats summonerBottom = new MonsterBattleStats(1, 1, MonsterType.SUMMONER, DamageType.NONE, 0, 0, 1, 1, null);
        MonsterBattleStats topMonster = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        MonsterBattleStats bottomMonster = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        Deck top = new Deck(summonerTop, topMonster);
        Deck bottom = new Deck(summonerBottom, bottomMonster);
        board.populateBoard(top, bottom);
        GameContext gameContext = new GameContext(board, null);
        BattleMaster battleMaster = new BattleMaster();

        DEFAULT_GAME_CONTEXT.getAttackService().attackSpecific(topMonster, DamageType.ATTACK, bottomMonster, DEFAULT_GAME_CONTEXT);

        assertEquals(Integer.valueOf(8), topMonster.getHealth().getValue());
        assertEquals(Integer.valueOf(9), bottomMonster.getHealth().getValue());
    }

    @Test
    public void poisonKillingOnRoundStartShouldKillAndRemoveMonster() {
        List<Ability> abilities = Collections.singletonList(new IsPoisonedAbility());
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(101, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 2, 1, abilities);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        MonsterBattleStats monsterBattleStats = new MonsterBattleStats(123, 3, MonsterType.MONSTER, DamageType.NONE, 0, 0, 100, 1, new ArrayList<>());
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStats);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(3, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(4, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(5, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(6, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(6, null);

        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom.getHealth().getValue());

        RoundPhase roundPhase = new RoundPhase();
        roundPhase.executeRound(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.printHistory();

        assertEquals(Integer.valueOf(0), monsterBattleStatsBottom.getHealth().getValue());
        assertTrue(monsterBattleStatsBottom.isDead());
        assertFalse(DEFAULT_GAME_CONTEXT.getAllMonsters().contains(monsterBattleStatsBottom));
        System.out.println(DEFAULT_BOARD.toSimpleString());
    }

    @Test
    public void itShouldDoubleAttackResurectedMonsterIfKilledFirstAttack() {
        List<Ability> abilities = Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.DOUBLE_STRIKE));
        List<Ability> resurrect =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.RESURRECT));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 100, 0, 100, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(2, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(3, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 100, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(4, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 101, 1, resurrect);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);

        RoundPhase roundPhase = new RoundPhase();
        roundPhase.executeTurn(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.printHistory();

        assertTrue(monsterBattleStatsBottom.isDead());
        assertEquals(Integer.valueOf(100), monsterBattleStatsBottom2.getHealth().getValue());
        assertSame(monsterBattleStatsBottom2, DEFAULT_BOARD.getBoardBottom().peekFromLocation(1));
    }

    @Test
    public void itShouldTrampleNextIfKillingBlowIsResurrected() {
        List<Ability> trample =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.TRAMPLE));
        List<Ability> resurrect =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.RESURRECT));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 1, 1, trample);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(2, 11, MonsterType.MONSTER, DamageType.NONE, 0, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(3, 22, MonsterType.MONSTER, DamageType.NONE, 0, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(4, 33, MonsterType.MONSTER, DamageType.NONE, 0, 0, 1, 1, resurrect);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(5, 44, MonsterType.MONSTER, DamageType.NONE, 0, 0, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(3, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(4, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(5, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(6, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(6, null);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom.getHealth().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom3.getHealth().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom4.getHealth().getValue());
        RoundPhase roundPhase = new RoundPhase();

        System.out.println(DEFAULT_BOARD.getBoardBottom());
        roundPhase.executeRound(DEFAULT_GAME_CONTEXT);
        System.out.println(DEFAULT_BOARD.getBoardBottom());
        DEFAULT_GAME_CONTEXT.printHistory();

        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom.getHealth().getValue());
        assertEquals(Integer.valueOf(0), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom3.getHealth().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom4.getHealth().getValue());

        assertSame(monsterBattleStatsBottom, DEFAULT_BOARD.getBoardBottom().peekFromLocation(1));
        assertSame(monsterBattleStatsBottom3, DEFAULT_BOARD.getBoardBottom().peekFromLocation(2));
        assertSame(monsterBattleStatsBottom4, DEFAULT_BOARD.getBoardBottom().peekFromLocation(3));
    }

    @Test
    public void trampleShouldGetReset() {
        List<Ability> trample =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.TRAMPLE));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 1, 1, trample);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(2, 11, MonsterType.MONSTER, DamageType.NONE, 0, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(3, 22, MonsterType.MONSTER, DamageType.NONE, 0, 0, 100, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(4, 33, MonsterType.MONSTER, DamageType.NONE, 0, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(5, 44, MonsterType.MONSTER, DamageType.NONE, 0, 0, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(3, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(4, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(5, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(6, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(6, null);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom.getHealth().getValue());
        assertEquals(Integer.valueOf(100), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom3.getHealth().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom4.getHealth().getValue());
        RoundPhase roundPhase = new RoundPhase();

        System.out.println(DEFAULT_BOARD.getBoardBottom());
        roundPhase.executeRound(DEFAULT_GAME_CONTEXT);
        System.out.println(DEFAULT_BOARD.getBoardBottom());

        assertEquals(Integer.valueOf(0), monsterBattleStatsBottom.getHealth().getValue());
        assertEquals(Integer.valueOf(90), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom3.getHealth().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom4.getHealth().getValue());

        roundPhase.executeRound(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.printHistory();

        assertEquals(Integer.valueOf(80), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom3.getHealth().getValue());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom4.getHealth().getValue());
    }

    @Test
    public void enrageShouldIncreaseEvenIfDebuffedBelow0() {
        assertTrue(false);
    }

    @Test
    public void itShouldReapplyBuffsOnResurrect() {
        List<Ability> resurrect =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.RESURRECT));
        List<Ability> abilities = new ArrayList<>();
        abilities.add(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SILENCE));
        abilities.add(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.STRENGTHEN));

        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(11, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(2, 11, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 5, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(3, 22, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(4, 33, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 1, 1, resurrect);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(5, 44, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 10, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(6), monsterBattleStatsBottom.getHealth().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom3.getHealth().getValue());
        assertEquals(Integer.valueOf(11), monsterBattleStatsBottom4.getHealth().getValue());
        RoundPhase roundPhase = new RoundPhase();

        System.out.println(DEFAULT_BOARD.getBoardBottom());
//        DEFAULT_GAME_CONTEXT.getAttackService().attack(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);
        roundPhase.executeTurn(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        System.out.println(DEFAULT_BOARD.getBoardBottom());
        DEFAULT_GAME_CONTEXT.log("Done.");
        DEFAULT_GAME_CONTEXT.printHistory();

        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom.getHealth().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom3.getHealth().getValue());
        assertEquals(Integer.valueOf(11), monsterBattleStatsBottom4.getHealth().getValue());
    }

    @Test
    public void deathByReflectRemovesFromTheBoard() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.MAGIC_REFLECT));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(101, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 10, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(202, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.newRound();
        assertEquals(Integer.valueOf(1), DEFAULT_BOARD.getBoardTop().peekFromLocation(1).getHealth().getValue());

        DEFAULT_GAME_CONTEXT.getAttackService().attack(monsterBattleStatsTop, DamageType.MAGIC, DEFAULT_GAME_CONTEXT);
        assertFalse(DEFAULT_GAME_CONTEXT.getAllMonsters().contains(monsterBattleStatsTop));
        assertTrue(monsterBattleStatsTop.isDead());
        System.out.println(DEFAULT_BOARD.toSimpleString());
    }

    @Test
    public void itShouldNotRemoveArmorFromProtectIfAlreadyDamaged() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.PROTECT));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(2, 1, MonsterType.MONSTER, DamageType.ATTACK, 4, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsTop2 = new MonsterBattleStats(3, 1, MonsterType.MONSTER, DamageType.MAGIC, 4, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(4, 1, MonsterType.MONSTER, DamageType.ATTACK, 10, 0, 1, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(5, 1, MonsterType.MONSTER, DamageType.MAGIC, 10, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(6, 1, MonsterType.MONSTER, DamageType.RANGED, 10, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(7, 1, MonsterType.MONSTER, DamageType.RANGED, 10, 10, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        assertEquals(Integer.valueOf(0), monsterBattleStatsBottom1.getArmor().getValue());
        assertEquals(Integer.valueOf(0), monsterBattleStatsBottom2.getArmor().getValue());
        assertEquals(Integer.valueOf(0), monsterBattleStatsBottom3.getArmor().getValue());
        assertEquals(Integer.valueOf(10), monsterBattleStatsBottom4.getArmor().getValue());
        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom1.getArmor().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom2.getArmor().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom3.getArmor().getValue());
        assertEquals(Integer.valueOf(12), monsterBattleStatsBottom4.getArmor().getValue());

        DEFAULT_GAME_CONTEXT.getAttackService().attackSpecific(monsterBattleStatsTop, DamageType.ATTACK, monsterBattleStatsBottom4, DEFAULT_GAME_CONTEXT);

        DEFAULT_GAME_CONTEXT.printHistory();
        assertEquals(Integer.valueOf(8), monsterBattleStatsBottom4.getArmor().getValue());

        DEFAULT_GAME_CONTEXT.getAttackService().attackSpecific(monsterBattleStatsTop2, DamageType.MAGIC, monsterBattleStatsBottom1, DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.log("Done");
        DEFAULT_GAME_CONTEXT.printHistory();

        assertTrue(monsterBattleStatsBottom1.isDead());
        assertEquals(Integer.valueOf(8), monsterBattleStatsBottom4.getArmor().getValue());
        assertEquals(Integer.valueOf(0), monsterBattleStatsBottom3.getArmor().getValue());
    }

    @Test
    public void itShouldReapplyBuffsOnRessurection() {
        List<Ability> abilities = new ArrayList<>();
        GameContext gameContext = new GameContext(DEFAULT_BOARD, Collections.singleton(GameRuleType.AIM_TRUE));
        gameContext.setTrackHistory(true);
        abilities.add(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.STRENGTHEN));
        abilities.add(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.PROTECT));
        List<Ability> resurrect =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.RESURRECT));
        List<Ability> snipe = Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SNIPE));
        List<Ability> sneak = Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.SNEAK));
        List<Ability> reach = Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.REACH));
        MonsterBattleStats monsterBattleStatsTop1 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 100, 0, 100, 2, null);
        MonsterBattleStats monsterBattleStatsTop2 = new MonsterBattleStats( 2, 1, MonsterType.MONSTER, DamageType.ATTACK, 100, 0, 100, 2, reach);
        MonsterBattleStats monsterBattleStatsTop3 = new MonsterBattleStats(3, 1, MonsterType.MONSTER, DamageType.RANGED, 2, 0, 100, 4, snipe);
        MonsterBattleStats monsterBattleStatsTop4 = new MonsterBattleStats(4, 1, MonsterType.MONSTER, DamageType.RANGED, 2, 0, 100, 4, snipe);
        MonsterBattleStats monsterBattleStatsTop5 = new MonsterBattleStats(5, 1, MonsterType.MONSTER, DamageType.ATTACK, 2, 0, 100, 4, sneak);
        MonsterBattleStats monsterBattleStatsTop6 = new MonsterBattleStats(6, 1, MonsterType.MONSTER, DamageType.ATTACK, 2, 0, 100, 4, sneak);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(10, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(11, 11, MonsterType.MONSTER, DamageType.NONE, 0, 5, 100, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(12, 11, MonsterType.MONSTER, DamageType.NONE, 0, 0, 100, 1, resurrect);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop1);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop2);
        DEFAULT_BOARD.getBoardTop().placeInSection(3, monsterBattleStatsTop3);
        DEFAULT_BOARD.getBoardTop().placeInSection(4, monsterBattleStatsTop4);
        DEFAULT_BOARD.getBoardTop().placeInSection(5, monsterBattleStatsTop5);
        DEFAULT_BOARD.getBoardTop().placeInSection(6, monsterBattleStatsTop6);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(6, null);
        System.out.println(gameContext);
        gameContext.setTrackHistory(true);
        assertTrue(monsterBattleStatsBottom1.getAbilities().size() == 2);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        gameInitializationPhase.execute(gameContext);

        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom1.getArmor().getValue());
        assertEquals(Integer.valueOf(7), monsterBattleStatsBottom2.getArmor().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom3.getArmor().getValue());

        RoundPhase roundPhase = new RoundPhase();
        roundPhase.executeRound(gameContext);
        gameContext.log("Done.");
        gameContext.printHistory();

        assertTrue(monsterBattleStatsBottom1.getAbilities().size() == 2);
        System.out.println(monsterBattleStatsBottom1.getArmor().toJson());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom1.getArmor().getValue());
        assertEquals(Integer.valueOf(5), monsterBattleStatsBottom2.getArmor().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom3.getArmor().getValue());
    }

    @Test
    public void itShouldRemoveDebuffsOnResurrect() {
        List<Ability> abilities = new ArrayList<>();
        abilities.add(new AfflictedAbility());
        abilities.add(new IsPoisonedAbility());
        abilities.add(new IsStunnedAbility());
        List<Ability> resurrect =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.RESURRECT));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 100, 0, 100, 1, null);
        MonsterBattleStats monsterBattleStatsBottom = new MonsterBattleStats(2, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 10, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(3, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 100, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(4, 11, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 101, 1, resurrect);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        System.out.println(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);
        assertTrue(monsterBattleStatsBottom.getAbilities().size() == 3);

        RoundPhase roundPhase = new RoundPhase();
        roundPhase.executeTurn(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.printHistory();

        assertTrue(monsterBattleStatsBottom.getAbilities().size() == 0);
    }

    @Test
    public void itShouldNotRemoveLastHealthOnRemovingStrengthen() {
        List<Ability> abilities =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.STRENGTHEN));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 100, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsTop2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.MAGIC, 1, 0, 10, 1, null);
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(1, 100, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, abilities);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(1, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, null);
        DEFAULT_BOARD.getBoardTop().placeInSection(1, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        assertEquals(Integer.valueOf(1), DEFAULT_BOARD.getBoardBottom().peekFromLocation(1).getHealth().getValue());
        assertEquals(Integer.valueOf(1), DEFAULT_BOARD.getBoardBottom().peekFromLocation(2).getHealth().getValue());
        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);
        assertSame(monsterBattleStatsBottom2, DEFAULT_BOARD.getBoardBottom().peekFromLocation(2));
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom3.getHealth().getValue());

        DEFAULT_GAME_CONTEXT.getAttackService().attackSpecific(monsterBattleStatsTop2, DamageType.MAGIC, monsterBattleStatsBottom3, DEFAULT_GAME_CONTEXT);
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom3.getHealth().getValue());
        DEFAULT_GAME_CONTEXT.getAttackService().attack(monsterBattleStatsTop, DamageType.ATTACK, DEFAULT_GAME_CONTEXT);

        assertFalse(monsterBattleStatsBottom3.isDead());
        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom3.getHealth().getValue());
    }

    @Test //https://splinterlands.io/?p=battle&id=a095542ede79afbe1851f22d552c9f99108e7328
    public void itShouldRetaliateOnKillingHitAndTrampleOnKillingRetaliate() {
//        assertTrue(false);
    }

    @Test
    public void itShouldApplyTheWeakenBuff() {
        List<Ability> weaken =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.WEAKEN));
        List<Ability> strengthen =  Collections.singletonList(DEFAULT_ABILITY_FACTORY.getAbility(AbilityType.STRENGTHEN));
        MonsterBattleStats monsterBattleStatsTop = new MonsterBattleStats(10, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 1, 1, weaken);
        MonsterBattleStats monsterBattleStatsTop2 = new MonsterBattleStats(11, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 1, 1, weaken);
        MonsterBattleStats monsterBattleStatsTop3 = new MonsterBattleStats(12, 1, MonsterType.MONSTER, DamageType.RANGED, 1, 0, 1, 1, weaken);
        MonsterBattleStats monsterBattleStatsBottomSummoner = new MonsterBattleStats(1, 1, MonsterType.SUMMONER, DamageType.ATTACK, 1, 0, 1, 1, Collections.singletonList(DEFAULT_ABILITY_FACTORY.getSummonerAbility(AbilityType.SUMMONER_HEALTH, 1)));
        MonsterBattleStats monsterBattleStatsBottom1 = new MonsterBattleStats(13, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 1, 1, strengthen);
        MonsterBattleStats monsterBattleStatsBottom2 = new MonsterBattleStats(14, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 3, 1, null);
        MonsterBattleStats monsterBattleStatsBottom3 = new MonsterBattleStats(15, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 4, 1, null);
        MonsterBattleStats monsterBattleStatsBottom4 = new MonsterBattleStats(16, 1, MonsterType.MONSTER, DamageType.ATTACK, 1, 0, 5, 1, null);
        DEFAULT_BOARD.getBoardBottom().placeInSection(0, monsterBattleStatsBottomSummoner);
        DEFAULT_BOARD.getBoardTop().placeInSection(2, monsterBattleStatsTop);
        DEFAULT_BOARD.getBoardTop().placeInSection(3, monsterBattleStatsTop2);
        DEFAULT_BOARD.getBoardTop().placeInSection(4, monsterBattleStatsTop3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(1, monsterBattleStatsBottom1);
        DEFAULT_BOARD.getBoardBottom().placeInSection(2, monsterBattleStatsBottom2);
        DEFAULT_BOARD.getBoardBottom().placeInSection(3, monsterBattleStatsBottom3);
        DEFAULT_BOARD.getBoardBottom().placeInSection(4, monsterBattleStatsBottom4);
        DEFAULT_BOARD.getBoardBottom().placeInSection(5, null);
        DEFAULT_GAME_CONTEXT.setTrackHistory(true);

        GameInitializationPhase gameInitializationPhase = new GameInitializationPhase();
        gameInitializationPhase.execute(DEFAULT_GAME_CONTEXT);
        DEFAULT_GAME_CONTEXT.printHistory();

        assertEquals(Integer.valueOf(1), monsterBattleStatsBottom1.getHealth().getValue());
        assertEquals(Integer.valueOf(2), monsterBattleStatsBottom2.getHealth().getValue());
        assertEquals(Integer.valueOf(3), monsterBattleStatsBottom3.getHealth().getValue());
        assertEquals(Integer.valueOf(4), monsterBattleStatsBottom4.getHealth().getValue());
    }
}